package app.bot.handler.callback.content.demo;

import app.bot.bot.CommandKey;
import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoStartCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.DEMO_MODE_BUTTON) ||
           callbackData.equals(TextMarker.BEFORE_DEMO_BEGIN_BUTTON);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.DEMO);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());
    CompositeResponse delayedResponse = new CompositeResponse(new ArrayList<>());

    //Todo: Add video or audio or voice. Text: This isn’t just a video. It’s part of the experience.
//    MediaResponse video = new MediaResponse(chatId, MediaType.VIDEO_NOTE, CommandKey.DEMO_CIRCLE);
    TextResponse text1 = new TextResponse(chatId, textService.format(TextMarker.DEMO_MEDIA), null);
    TextResponse text2 = new TextResponse(chatId, textService.format(TextMarker.DEMO_MOMENT), null);

//    compositeResponse.responses().add(video);
    compositeResponse.responses().add(text1);
    compositeResponse.responses().add(text2);

    MediaResponse delayedVideo = new MediaResponse(chatId, MediaType.VOICE, CommandKey.DEMO_AUDIO);
    TextResponse delayedText = new TextResponse(chatId, textService.format(TextMarker.DEMO_ACCESS),
        KeyboardFactory.from(List.of(
             KeyboardOption.callback(textService.format(TextMarker.DEMO_ACCESS_BUTTON_UNLOCK), TextMarker.DEMO_ACCESS_BUTTON_UNLOCK),
             KeyboardOption.callback(textService.format(TextMarker.DEMO_ACCESS_BUTTON_CONTINUE), TextMarker.DEMO_ACCESS_BUTTON_CONTINUE))));

    delayedResponse.responses().add(delayedVideo);
    delayedResponse.responses().add(delayedText);

    return new SendWithDelayedResponse(compositeResponse, delayedResponse, Duration.ofSeconds(3));
  }
}
