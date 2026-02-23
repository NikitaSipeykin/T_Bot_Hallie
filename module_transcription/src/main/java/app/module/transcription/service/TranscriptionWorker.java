package app.module.transcription.service;

import app.core.transcription.TranscriptionCompletedEvent;
import app.core.transcription.TranscriptionFailedEvent;
import app.core.transcription.TranscriptionResult;
import app.module.transcription.dao.JobStatus;
import app.module.transcription.dao.TranscriptionJob;
import app.module.transcription.dao.TranscriptionJobResult;
import app.module.transcription.repository.TranscriptionJobRepository;
import app.module.transcription.repository.TranscriptionResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptionWorker {

  private final TranscriptionJobRepository jobRepository;
  private final TranscriptionResultRepository resultRepository;
  private final AudioConversionService conversionService;
  private final WhisperClient whisperClient;
  private final KubernetesScaler kubernetesScaler;
  private final ApplicationEventPublisher eventPublisher;

  @Value("${transcription.temp-dir:/tmp/transcription}")
  private String tempDir;

  @Value("${bot.token}")
  private String botToken;

  // Максимальное время ожидания старта Whisper — 3 минуты
  private static final int WHISPER_START_TIMEOUT_SEC = 180;
  // Время простоя после которого Whisper выключается — 10 минут
  private static final long IDLE_SHUTDOWN_MS = 10 * 60 * 1000L;

  // Только 1 задача одновременно
  private final Semaphore semaphore = new Semaphore(1);

  // Время последней обработанной задачи (для автовыключения)
  private final AtomicLong lastJobCompletedAt = new AtomicLong(0);

  // Флаг — запущен ли Whisper нами
  private volatile boolean whisperStartedByUs = false;

  @Scheduled(fixedDelay = 5000)
  public void processNextJob() {
    if (!semaphore.tryAcquire()) return;

    TranscriptionJob job = null;
    try {
      job = jobRepository.findNextPendingJob().orElse(null);

      if (job == null) {
        // Нет задач — проверяем не пора ли выключить Whisper
        maybeShutdownWhisper();
        return;
      }

      // Есть задача — убеждаемся что Whisper запущен
      ensureWhisperRunning();
      process(job);

    } catch (Exception e) {
      log.error("Unexpected worker error", e);
      if (job != null) markError(job, "Internal error: " + e.getMessage());
    } finally {
      semaphore.release();
    }
  }

  private void ensureWhisperRunning() throws InterruptedException {
    if (whisperClient.isAvailable()) return;

    log.info("Whisper not running, starting...");
    kubernetesScaler.scaleUp();
    whisperStartedByUs = true;

    // Ждём пока Whisper поднимется
    for (int i = 0; i < WHISPER_START_TIMEOUT_SEC; i++) {
      Thread.sleep(1000);
      if (whisperClient.isAvailable()) {
        log.info("Whisper is ready after {}s", i + 1);
        return;
      }
    }
    throw new RuntimeException("Whisper did not start up " + WHISPER_START_TIMEOUT_SEC + " seconds");
  }

  private void maybeShutdownWhisper() {
    if (!whisperStartedByUs) return;
    if (!whisperClient.isAvailable()) return;

    long idleMs = System.currentTimeMillis() - lastJobCompletedAt.get();
    if (lastJobCompletedAt.get() > 0 && idleMs > IDLE_SHUTDOWN_MS) {
      log.info("Queue empty for {}min, shutting down Whisper", idleMs / 60000);
      kubernetesScaler.scaleDown();
      whisperStartedByUs = false;
      lastJobCompletedAt.set(0);
    }
  }

  private void process(TranscriptionJob job) {
    log.info("Processing job {}", job.getId());
    Path inputFile = null;
    Path wavFile = null;

    try {
      // Шаг 1: получаем файл (либо скачиваем с Telegram, либо уже локальный)
      setStatus(job, JobStatus.DOWNLOADING);
      if (job.getLocalFilePath() != null) {
        inputFile = Path.of(job.getLocalFilePath());
        log.info("Using local file: {}", inputFile.getFileName());
      } else {
        inputFile = downloadFromTelegram(job);
      }

      // Шаг 2: конвертируем в WAV
      setStatus(job, JobStatus.CONVERTING);
      wavFile = conversionService.convertToWav(inputFile, job.getId().toString());

      // Шаг 3: транскрибируем
      setStatus(job, JobStatus.TRANSCRIBING);
      long start = System.currentTimeMillis();
      String fullText = whisperClient.transcribe(wavFile);
      long elapsed = System.currentTimeMillis() - start;

      // Шаг 4: сохраняем результат
      String summary = buildSummary(fullText);
      int wordCount = countWords(fullText);

      resultRepository.save(TranscriptionJobResult.builder()
          .jobId(job.getId())
          .fullText(fullText)
          .summary(summary)
          .wordCount(wordCount)
          .processingTimeMs(elapsed)
          .build());

      job.setStatus(JobStatus.DONE);
      job.setCompletedAt(Instant.now());
      jobRepository.save(job);

      lastJobCompletedAt.set(System.currentTimeMillis());

      eventPublisher.publishEvent(new TranscriptionCompletedEvent(
          new TranscriptionResult(
              job.getId(), job.getChatId(),
              fullText, summary, wordCount, elapsed
          )
      ));

    } catch (Exception e) {
      log.error("Job {} failed: {}", job.getId(), e.getMessage(), e);
      if (job.getRetryCount() < 3) {
        job.setRetryCount(job.getRetryCount() + 1);
        setStatus(job, JobStatus.PENDING);
        log.info("Job {} retry {}/3", job.getId(), job.getRetryCount());
      } else {
        markError(job, e.getMessage());
      }
    } finally {
      // Удаляем временные файлы только если файл не локальный (из веб-панели)
      if (job.getLocalFilePath() == null) {
        conversionService.deleteSilently(inputFile);
      }
      conversionService.deleteSilently(wavFile);
    }
  }

  private Path downloadFromTelegram(TranscriptionJob job) throws Exception {
    String fileInfoUrl = "https://api.telegram.org/bot" + botToken
                         + "/getFile?file_id=" + job.getTelegramFileId();

    var response = new URL(fileInfoUrl).openStream().readAllBytes();
    String json = new String(response);
    int pathStart = json.indexOf("\"file_path\":\"") + 13;
    int pathEnd = json.indexOf("\"", pathStart);
    String filePath = json.substring(pathStart, pathEnd);

    String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + filePath;

    Path tempDirPath = Path.of(tempDir);
    Files.createDirectories(tempDirPath);
    Path localFile = tempDirPath.resolve(job.getId() + "_" + job.getFileName());

    try (InputStream in = new URL(fileUrl).openStream()) {
      Files.copy(in, localFile, StandardCopyOption.REPLACE_EXISTING);
    }

    log.info("Downloaded {} bytes to {}", Files.size(localFile), localFile.getFileName());
    return localFile;
  }

  private void setStatus(TranscriptionJob job, JobStatus status) {
    job.setStatus(status);
    jobRepository.save(job);
  }

  private void markError(TranscriptionJob job, String reason) {
    job.setStatus(JobStatus.ERROR);
    job.setErrorMessage(reason);
    job.setCompletedAt(Instant.now());
    jobRepository.save(job);

    eventPublisher.publishEvent(
        new TranscriptionFailedEvent(job.getId(), job.getChatId(), reason)
    );
  }

  private String buildSummary(String text) {
    if (text == null || text.isBlank()) return "Text is empty";
    if (text.length() < 400) return text;

    String[] sentences = text.split("(?<=[.!?])\\s+");
    if (sentences.length <= 5) return text;

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 3; i++) sb.append(sentences[i]).append(" ");
    sb.append("\n[...]\n");
    for (int i = sentences.length - 2; i < sentences.length; i++) sb.append(sentences[i]).append(" ");
    return sb.toString().trim();
  }

  private int countWords(String text) {
    if (text == null || text.isBlank()) return 0;
    return text.trim().split("\\s+").length;
  }
}
