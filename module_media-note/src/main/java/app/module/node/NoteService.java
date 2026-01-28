package app.module.node;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Service
public class NoteService {

  public SendVideoNote buildVideoNote(Long chatId, File videoFile) {
    SendVideoNote note = new SendVideoNote();
    note.setChatId(chatId);
    note.setVideoNote(new InputFile(videoFile));

    return note;
  }

  public SendVideo buildVideo(Long chatId, File videoFile, Integer width, Integer height) {
    SendVideo video = new SendVideo();
    video.setChatId(chatId);
    video.setVideo(new InputFile(videoFile));
    video.setWidth(width);
    video.setHeight(height);

    return video;
  }

  public SendVoice buildVoice(Long chatId, File audioFile) {
    SendVoice voice = new SendVoice();
    voice.setChatId(chatId);
    voice.setVoice(new InputFile(audioFile));

    return voice;
  }

  public SendAudio buildAudio(Long chatId, File audioFile) {
    SendAudio voice = new SendAudio();
    voice.setChatId(chatId);
    voice.setAudio(new InputFile(audioFile));

    return voice;
  }

  public SendDocument buildPdf(Long chatId, File pdfFile) {
    SendDocument document = new SendDocument();
    document.setChatId(chatId);
    document.setDocument(new InputFile(pdfFile));

    return document;
  }
}