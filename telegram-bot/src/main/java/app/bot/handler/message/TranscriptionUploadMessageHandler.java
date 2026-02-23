package app.bot.handler.message;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.transcription.TranscriptionCommand;
import app.core.transcription.TranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptionUploadMessageHandler implements MessageHandler {

  private static final long MAX_FILE_SIZE = 20 * 1024 * 1024L;

  private final UserStateService userStateService;
  private final TranscriptionService transcriptionService;

  @Override
  public UserState supports() {
    return UserState.TRANSCRIPTION_UPLOAD;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    Long userId = message.getFrom().getId();

    FileInfo fileInfo = extractFileInfo(message);

    if (fileInfo == null) {
      return new TextResponse(chatId,
          "⚠️ Please send an audio or video file..\n\n" +
          "Supported: voice, audio, video, documents with audio/video.",
          null
      );
    }

    if (fileInfo.fileSize != null && fileInfo.fileSize > MAX_FILE_SIZE) {
      return new TextResponse(chatId,
          "⚠️ The file is too large.: " + (fileInfo.fileSize / 1024 / 1024) + " MB\n\n" +
          "Telegram limits files to 20MB.\n" +
          "For large files, use the web panel: /transcription",
          null
      );
    }

    transcriptionService.submit(TranscriptionCommand.fromTelegram(
        userId, chatId,
        fileInfo.fileId, fileInfo.fileName,
        fileInfo.fileSize, fileInfo.duration
    ));

    userStateService.setState(chatId, UserState.DEFAULT);

    String durationStr = fileInfo.duration != null
        ? "\n⏱ Duration: " + formatDuration(fileInfo.duration)
        : "";

    return new TextResponse(chatId,
        "✅ File accepted!\n" +
        "📁 " + fileInfo.fileName + durationStr + "\n\n" +
        "⏳ Placed in queue. I will notify you when it is ready..\n\n" +
        "Status: /transcribe_status",
        null
    );
  }

  private FileInfo extractFileInfo(Message message) {
    if (message.hasVoice()) {
      Voice v = message.getVoice();
      return new FileInfo(v.getFileId(),
          "voice_" + System.currentTimeMillis() + ".ogg",
          v.getFileSize() != null ? v.getFileSize().longValue() : null,
          v.getDuration());
    }
    if (message.hasAudio()) {
      Audio a = message.getAudio();
      return new FileInfo(a.getFileId(),
          a.getFileName() != null ? a.getFileName() : "audio.mp3",
          a.getFileSize() != null ? a.getFileSize().longValue() : null,
          a.getDuration());
    }
    if (message.hasVideo()) {
      Video v = message.getVideo();
      return new FileInfo(v.getFileId(),
          "video_" + System.currentTimeMillis() + ".mp4",
          v.getFileSize() != null ? v.getFileSize().longValue() : null,
          v.getDuration());
    }
    if (message.hasDocument()) {
      Document d = message.getDocument();
      String mime = d.getMimeType() != null ? d.getMimeType() : "";
      if (mime.startsWith("audio/") || mime.startsWith("video/") || isAudioFile(d.getFileName())) {
        return new FileInfo(d.getFileId(),
            d.getFileName() != null ? d.getFileName() : "file",
            d.getFileSize(),
            null);
      }
    }
    return null;
  }

  private boolean isAudioFile(String name) {
    if (name == null) return false;
    String n = name.toLowerCase();
    return n.endsWith(".mp3") || n.endsWith(".ogg") || n.endsWith(".wav")
           || n.endsWith(".m4a") || n.endsWith(".mp4") || n.endsWith(".webm")
           || n.endsWith(".flac") || n.endsWith(".aac") || n.endsWith(".mov");
  }

  private String formatDuration(int seconds) {
    if (seconds < 60) return seconds + " sec";
    return (seconds / 60) + " min " + (seconds % 60) + " sec";
  }

  private record FileInfo(String fileId, String fileName, Long fileSize, Integer duration) {}
}
