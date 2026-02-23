package app.module.transcription.repository;

import app.module.transcription.dao.TranscriptionJobResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TranscriptionResultRepository extends JpaRepository<TranscriptionJobResult, UUID> {

  Optional<TranscriptionJobResult> findByJobId(UUID jobId);
}
