package app.core.transcription;

import java.util.UUID;

public record TranscriptionFailedEvent(
    UUID jobId,
    Long chatId,
    String reason
) {}
