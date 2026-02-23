package app.bot.handler.command;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.transcription.TranscriptionService;
import app.module.transcription.dao.JobStatus;
import app.module.transcription.repository.TranscriptionJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class TranscribeCommandHandler implements CommandHandler {

  private final UserStateService userStateService;
  private final TranscriptionJobRepository jobRepository;

  @Override
  public String command() {
    return "/transcribe";
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    Long userId = message.getFrom().getId();
    String text = message.getText();

    if (text.startsWith("/transcribe_status")) return handleStatus(chatId, userId);

    // /transcribe — переходим в режим ожидания файла
    userStateService.setState(chatId, UserState.TRANSCRIPTION_UPLOAD);

    return new TextResponse(chatId,
        "🎙 Transcription mode\n\n" +
        "Send me:\n" +
        "• 🎤 Voice message\n" +
        "• 🎵 Audio file (mp3, ogg, m4a)\n" +
        "• 🎬 Video file (mp4)\n\n" +
        "⚠️ Limit: up to 20MB\n\n" +
        "To cancel: /menu",
        null
    );
  }

  private BotResponse handleStatus(Long chatId, Long userId) {
    var jobs = jobRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);

    if (jobs.isEmpty()) {
      return new TextResponse(chatId, "You don't have any transcription tasks yet..", null);
    }

    StringBuilder sb = new StringBuilder("📋 Latest tasks:\n\n");
    jobs.forEach(job -> {
      String emoji = switch (job.getStatus()) {
        case PENDING      -> "⏳";
        case DOWNLOADING  -> "📥";
        case CONVERTING   -> "🔄";
        case TRANSCRIBING -> "🎙️";
        case DONE         -> "✅";
        case ERROR        -> "❌";
      };
      sb.append(emoji).append(" ")
          .append(job.getFileName() != null ? job.getFileName() : "file")
          .append(" — ").append(job.getStatus()).append("\n");
    });

    return new TextResponse(chatId, sb.toString(), null);
  }
}
