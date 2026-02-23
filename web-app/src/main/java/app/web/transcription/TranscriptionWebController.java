package app.web.transcription;

import app.core.transcription.TranscriptionCommand;
import app.core.transcription.TranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transcription")
@RequiredArgsConstructor
@Slf4j
public class TranscriptionWebController {

  private final TranscriptionService transcriptionService;

  @Value("${transcription.temp-dir:/tmp/transcription}")
  private String tempDir;

  @Value("${BOT_ADMIN_ID}")
  private Long adminChatId;

  /**
   * Принимает файл (включая .mov, .mp4, .mp3 и др.),
   * сохраняет на диск и ставит в очередь транскрибации.
   * Результат придёт администратору в Telegram.
   */
  @PostMapping("/upload")
  public ResponseEntity<Map<String, Object>> upload(
      @RequestParam("file") MultipartFile file
  ) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Файл пустой"));
    }

    String originalName = file.getOriginalFilename();
    if (originalName == null) originalName = "upload_" + System.currentTimeMillis();

    try {
      // Сохраняем файл во временную папку
      Path tempDirPath = Path.of(tempDir);
      Files.createDirectories(tempDirPath);

      String uniqueName = UUID.randomUUID() + "_" + originalName;
      Path savedFile = tempDirPath.resolve(uniqueName);
      file.transferTo(savedFile);

      log.info("Web upload saved: {} ({} bytes)", uniqueName, Files.size(savedFile));

      // Ставим в очередь
      UUID jobId = transcriptionService.submit(
          TranscriptionCommand.fromWeb(
              adminChatId,
              originalName,
              file.getSize(),
              savedFile.toString()
          )
      );

      return ResponseEntity.ok(Map.of(
          "jobId", jobId.toString(),
          "fileName", originalName,
          "message", "Файл принят. Результат придёт в Telegram."
      ));

    } catch (Exception e) {
      log.error("Web upload failed: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body(Map.of("error", "Ошибка загрузки: " + e.getMessage()));
    }
  }

  /**
   * Статус задачи по ID — для polling из JS
   */
  @GetMapping("/status/{jobId}")
  public ResponseEntity<Map<String, String>> status(@PathVariable String jobId) {
    try {
      String status = transcriptionService.getStatusText(UUID.fromString(jobId));
      return ResponseEntity.ok(Map.of("status", status));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Задача не найдена"));
    }
  }
}
