package app.bot.bot.responce;

public record VideoResponse(
    Long chatId,
    MediaType type,
    String fileId,
    Integer width,
    Integer height
) implements BotResponse {}