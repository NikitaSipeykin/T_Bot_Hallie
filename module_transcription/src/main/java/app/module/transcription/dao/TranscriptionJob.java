package app.module.transcription.dao;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transcription_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranscriptionJob {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "chat_id", nullable = false)
  private Long chatId;

  @Column(name = "telegram_file_id")
  private String telegramFileId;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "file_size")
  private Long fileSize;

  @Column(name = "duration_seconds")
  private Integer durationSeconds;

  /**
   * Путь к файлу на диске — используется для задач из веб-панели.
   * Если null — файл скачивается с Telegram API.
   */
  @Column(name = "local_file_path")
  private String localFilePath;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private JobStatus status;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Column(name = "retry_count")
  @Builder.Default
  private Integer retryCount = 0;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "completed_at")
  private Instant completedAt;

  @PrePersist
  void prePersist() {
    createdAt = Instant.now();
    if (status == null) status = JobStatus.PENDING;
    if (retryCount == null) retryCount = 0;
  }
}
