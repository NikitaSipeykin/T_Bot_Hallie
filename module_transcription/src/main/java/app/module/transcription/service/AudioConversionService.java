package app.module.transcription.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class AudioConversionService {

  @Value("${transcription.temp-dir:/tmp/transcription}")
  private String tempDir;

  /**
   * Конвертирует любой аудио/видео файл в WAV 16kHz mono через FFmpeg.
   * Это оптимальный формат для Whisper.
   */
  public Path convertToWav(Path inputFile, String jobId) throws Exception {
    Path tempDirPath = Path.of(tempDir);
    Files.createDirectories(tempDirPath);

    Path outputFile = tempDirPath.resolve(jobId + "_converted.wav");

    ProcessBuilder pb = new ProcessBuilder(
        "ffmpeg",
        "-i", inputFile.toString(),
        "-ar", "16000",       // 16kHz — требование Whisper
        "-ac", "1",           // mono
        "-c:a", "pcm_s16le",  // 16-bit PCM
        "-y",                 // перезаписать если существует
        outputFile.toString()
    );
    pb.redirectErrorStream(true);

    Process process = pb.start();
    String output = new String(process.getInputStream().readAllBytes());
    int exitCode = process.waitFor();

    if (exitCode != 0) {
      log.error("FFmpeg failed (code {}): {}", exitCode, output);
      throw new RuntimeException("FFmpeg conversion failed. Code: " + exitCode);
    }

    log.info("Converted {} -> {}", inputFile.getFileName(), outputFile.getFileName());
    return outputFile;
  }

  public void deleteSilently(Path file) {
    try {
      if (file != null) Files.deleteIfExists(file);
    } catch (Exception e) {
      log.warn("Could not delete temp file {}: {}", file, e.getMessage());
    }
  }
}
