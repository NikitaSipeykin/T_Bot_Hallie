package app.module.transcription.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

@Service
@Slf4j
public class WhisperClient {

  private final RestTemplate restTemplate;
  private final String whisperUrl;

  public WhisperClient(
      @Value("${transcription.whisper.url:http://whisper-server:8085}") String whisperUrl
  ) {
    this.whisperUrl = whisperUrl;
    // Большой таймаут: 2 часа аудио ≈ до 6 часов обработки на CPU
    this.restTemplate = new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofSeconds(30))
        .setReadTimeout(Duration.ofHours(4))
        .build();
  }

  /**
   * Отправляет WAV файл в whisper-server и возвращает текст транскрибации.
   */
  public String transcribe(Path wavFile) {
    log.info("Sending to Whisper: {}", wavFile.getFileName());
    long start = System.currentTimeMillis();

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", new FileSystemResource(wavFile.toFile()));
    body.add("response_format", "json");
    body.add("language", "ru");
    body.add("task", "transcribe");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(
          whisperUrl + "/inference",
          new HttpEntity<>(body, headers),
          Map.class
      );

      log.info("Whisper done in {}ms", System.currentTimeMillis() - start);

      Map<?, ?> responseBody = response.getBody();
      if (responseBody == null || !responseBody.containsKey("text")) {
        throw new RuntimeException("Blank or incorrect response from Whisper");
      }

      return responseBody.get("text").toString().trim();

    } catch (Exception e) {
      log.error("Whisper transcription failed: {}", e.getMessage(), e);
      throw new RuntimeException("Transcription error: " + e.getMessage(), e);
    }
  }

  public boolean isAvailable() {
    try {
      return restTemplate
          .getForEntity(whisperUrl + "/health", Map.class)
          .getStatusCode()
          .is2xxSuccessful();
    } catch (Exception e) {
      log.warn("Whisper health check failed: {}", e.getMessage());
      return false;
    }
  }
}
