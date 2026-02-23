package app.module.transcription.repository;

import app.module.transcription.dao.JobStatus;
import app.module.transcription.dao.TranscriptionJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TranscriptionJobRepository extends JpaRepository<TranscriptionJob, UUID> {

  @Query(value = """
        SELECT * FROM transcription_jobs
        WHERE status = 'PENDING'
        ORDER BY retry_count ASC, created_at ASC
        LIMIT 1
        """, nativeQuery = true)
  Optional<TranscriptionJob> findNextPendingJob();

  List<TranscriptionJob> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);

  Optional<TranscriptionJob> findFirstByUserIdAndStatusOrderByCreatedAtDesc(
      Long userId, JobStatus status
  );
}
