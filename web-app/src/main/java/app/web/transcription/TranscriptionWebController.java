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

  @PostMapping("/upload")
  public ResponseEntity<Map<String, Object>> upload(
      @RequestParam("file") MultipartFile file
  ) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "File is empty"));
    }

    String originalName = file.getOriginalFilename();
    if (originalName == null) originalName = "upload_" + System.currentTimeMillis();

    try {
      Path tempDirPath = Path.of(tempDir);
      Files.createDirectories(tempDirPath);

      // Save raw file temporarily
      String uniqueId = UUID.randomUUID().toString();
      String ext = getExtension(originalName);
      Path rawFile = tempDirPath.resolve(uniqueId + "_raw." + ext);
      file.transferTo(rawFile);
      log.info("Web upload saved raw: {} ({} bytes)", rawFile.getFileName(), Files.size(rawFile));

      // Convert video/heavy formats to mp3 immediately
      Path inputFile;
      String finalName;
      if (shouldConvert(originalName)) {
        Path mp3File = tempDirPath.resolve(uniqueId + "_converted.mp3");
        convertToMp3(rawFile, mp3File);
        Files.deleteIfExists(rawFile);
        inputFile = mp3File;
        finalName = stripExtension(originalName) + ".mp3";
        log.info("Converted to mp3: {} ({} bytes)", mp3File.getFileName(), Files.size(mp3File));
      } else {
        inputFile = rawFile;
        finalName = originalName;
      }

      UUID jobId = transcriptionService.submit(
          TranscriptionCommand.fromWeb(
              adminChatId,
              finalName,
              Files.size(inputFile),
              inputFile.toString()
          )
      );

      return ResponseEntity.ok(Map.of(
          "jobId", jobId.toString(),
          "fileName", finalName,
          "message", "File accepted. Result will be sent to Telegram."
      ));

    } catch (Exception e) {
      log.error("Web upload failed: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body(Map.of("error", "Error: " + e.getMessage()));
    }
  }

  @GetMapping("/status/{jobId}")
  public ResponseEntity<Map<String, String>> status(@PathVariable String jobId) {
    try {
      String status = transcriptionService.getStatusText(UUID.fromString(jobId));
      return ResponseEntity.ok(Map.of("status", status));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Job not found"));
    }
  }

  private void convertToMp3(Path input, Path output) throws Exception {
    ProcessBuilder pb = new ProcessBuilder(
        "ffmpeg", "-y",
        "-i", input.toString(),
        "-vn",           // no video
        "-ar", "16000",  // 16kHz optimal for Whisper
        "-ac", "1",      // mono
        "-b:a", "64k",   // bitrate sufficient for speech
        output.toString()
    );
    pb.redirectErrorStream(true);
    Process process = pb.start();
    String ffmpegOutput = new String(process.getInputStream().readAllBytes());
    int code = process.waitFor();
    if (code != 0) {
      log.error("FFmpeg conversion failed (code {}): {}", code, ffmpegOutput);
      throw new RuntimeException("Conversion failed. FFmpeg code: " + code);
    }
  }

  private boolean shouldConvert(String filename) {
    if (filename == null) return false;
    String lower = filename.toLowerCase();
    return lower.endsWith(".mov") || lower.endsWith(".mp4")
           || lower.endsWith(".avi") || lower.endsWith(".mkv")
           || lower.endsWith(".webm") || lower.endsWith(".m4a")
           || lower.endsWith(".m4v") || lower.endsWith(".wmv");
  }

  private String getExtension(String filename) {
    int dot = filename.lastIndexOf('.');
    return dot >= 0 ? filename.substring(dot + 1) : "bin";
  }

  private String stripExtension(String filename) {
    int dot = filename.lastIndexOf('.');
    return dot >= 0 ? filename.substring(0, dot) : filename;
  }
}
