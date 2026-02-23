package app.module.transcription;

import app.core.transcription.TranscriptionCommand;
import app.core.transcription.TranscriptionService;
import app.module.transcription.dao.JobStatus;
import app.module.transcription.dao.TranscriptionJob;
import app.module.transcription.repository.TranscriptionJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TranscriptionServiceImpl implements TranscriptionService {

  private final TranscriptionJobRepository jobRepository;

  @Override
  public UUID submit(TranscriptionCommand command) {
    TranscriptionJob job = TranscriptionJob.builder()
        .userId(command.userId())
        .chatId(command.chatId())
        .telegramFileId(command.telegramFileId())
        .fileName(command.fileName())
        .fileSize(command.fileSize())
        .durationSeconds(command.durationSeconds())
        .localFilePath(command.localFilePath())
        .status(JobStatus.PENDING)
        .build();

    return jobRepository.save(job).getId();
  }

  @Override
  public String getStatusText(UUID jobId) {
    return jobRepository.findById(jobId)
        .map(job -> switch (job.getStatus()) {
          case PENDING      -> "⏳ В очереди";
          case DOWNLOADING  -> "📥 Скачиваю файл...";
          case CONVERTING   -> "🔄 Конвертирую аудио...";
          case TRANSCRIBING -> "🎙️ Транскрибирую...";
          case DONE         -> "✅ Готово";
          case ERROR        -> "❌ Ошибка: " + job.getErrorMessage();
        })
        .orElse("Задача не найдена");
  }
}
