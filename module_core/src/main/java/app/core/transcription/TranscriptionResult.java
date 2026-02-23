package app.core.transcription;

import java.util.UUID;

public record TranscriptionResult(
    UUID jobId,
    Long chatId,
    String fullText,
    String summary,
    int wordCount,
    long processingTimeMs
) {}
