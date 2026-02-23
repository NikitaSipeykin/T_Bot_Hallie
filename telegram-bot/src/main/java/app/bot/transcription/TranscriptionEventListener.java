package app.bot.transcription;

import app.core.transcription.TranscriptionCompletedEvent;
import app.core.transcription.TranscriptionFailedEvent;
import app.core.transcription.TranscriptionResult;
import app.bot.sender.TelegramMessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Слушает события из module_transcription и отправляет
 * результаты пользователю через Telegram.
 *
 * Единственный класс в telegram-bot который знает о результатах транскрибации.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptionEventListener {

  private final TelegramMessageSender messageSender;
  private final ObjectProvider<TelegramLongPollingBot> botProvider;

  @EventListener
  public void onCompleted(TranscriptionCompletedEvent event) {
    TranscriptionResult result = event.result();
    Long chatId = result.chatId();

    // Статистика и саммари
    String stats = String.format(
        "✅ Transcription complete!\n\n" +
        "📊 Words: %d\n" +
        "⏱ Processing time: %s\n\n" +
        "📌 Summary:\n%s",
        result.wordCount(),
        formatDuration(result.processingTimeMs()),
        result.summary()
    );
    messageSender.sendText(chatId, stats);

    // Полный текст
    String fullText = result.fullText();
    if (fullText.length() <= 3500) {
      messageSender.sendText(chatId, "📝 Full text:\n\n" + fullText);
    } else {
      // Отправляем как файл — текст слишком длинный для сообщения
      sendAsFile(chatId, fullText, result.jobId().toString().substring(0, 8));
    }

    messageSender.sendText(chatId,
        "💡 Send a new file for transcription\n" +
        "📋 History: /transcribe_status"
    );
  }

  @EventListener
  public void onFailed(TranscriptionFailedEvent event) {
    messageSender.sendText(event.chatId(),
        "❌ Failed to process the file.\n" +
        "Reason: " + event.reason() + "\n\n" +
        "Try sending the file again: /transcribe"
    );
  }

  private void sendAsFile(Long chatId, String text, String jobShortId) {
    try {
      byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
      String fileName = "transcription_" + jobShortId + ".txt";

      SendDocument doc = SendDocument.builder()
          .chatId(chatId.toString())
          .document(new InputFile(new ByteArrayInputStream(bytes), fileName))
          .caption("📝 Full text of the transcript")
          .build();

      botProvider.getObject().execute(doc);
    } catch (Exception e) {
      log.error("Failed to send transcript file to chatId={}", chatId, e);
      messageSender.sendText(chatId,
          "📝 Start of text:\n\n" +
          text.substring(0, 3000) + "\n\n[text truncated]"
      );
    }
  }

  private String formatDuration(long ms) {
    if (ms < 1000) return ms + " ms";
    if (ms < 60_000) return (ms / 1000) + " s";
    return (ms / 60_000) + " m " + ((ms % 60_000) / 1000) + " s";
  }
}
