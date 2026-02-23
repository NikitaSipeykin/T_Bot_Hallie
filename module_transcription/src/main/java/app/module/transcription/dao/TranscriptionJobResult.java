package app.module.transcription.dao;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transcription_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranscriptionJobResult {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "job_id", nullable = false)
  private UUID jobId;

  @Column(name = "full_text", columnDefinition = "TEXT")
  private String fullText;

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  @Column(name = "word_count")
  private Integer wordCount;

  @Column(name = "processing_time_ms")
  private Long processingTimeMs;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void prePersist() {
    createdAt = Instant.now();
  }
}
