package app.core.transcription;

/**
 * Команда создания задачи транскрибации.
 * localFilePath — заполняется для задач из веб-панели (файл уже на диске).
 * telegramFileId — заполняется для задач из Telegram.
 */
public record TranscriptionCommand(
    Long userId,
    Long chatId,
    String telegramFileId,
    String fileName,
    Long fileSize,
    Integer durationSeconds,
    String localFilePath   // null для Telegram задач
) {
  // Фабричный метод для Telegram
  public static TranscriptionCommand fromTelegram(
      Long userId, Long chatId,
      String fileId, String fileName,
      Long fileSize, Integer duration
  ) {
    return new TranscriptionCommand(userId, chatId, fileId, fileName, fileSize, duration, null);
  }

  // Фабричный метод для веб-панели
  public static TranscriptionCommand fromWeb(
      Long adminChatId,
      String fileName,
      Long fileSize,
      String localFilePath
  ) {
    return new TranscriptionCommand(adminChatId, adminChatId, null, fileName, fileSize, null, localFilePath);
  }
}
