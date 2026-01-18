package app.bot.handler.callback.content.demo;

import app.bot.bot.CommandKey;
import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.content.AccessServiceImpl;
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
public class DemoAccessCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final AccessServiceImpl accessService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.DEMO_ACCESS_BUTTON_UNLOCK);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.DEMO_ACCESS);
    accessService.grantAccess(chatId);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());
    CompositeResponse delayedResponse = new CompositeResponse(new ArrayList<>());

    MediaResponse audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.ACCESS_AUDIO);
    MediaResponse video = new MediaResponse(chatId, MediaType.VIDEO, CommandKey.ACCESS_VIDEO);
    TextResponse text1 = new TextResponse(chatId, textService.format(TextMarker.ACCESS_PAYMENT), null);
    TextResponse text2 = new TextResponse(chatId, textService.format(TextMarker.ACCESS_NEXT),
        KeyboardFactory.from(List.of(
            new KeyboardOption(textService.format(TextMarker.ACCESS_BUTTON_NEXT), TextMarker.EMAIL))));

    compositeResponse.responses().add(audio);
    compositeResponse.responses().add(video);
    delayedResponse.responses().add(text1);
    delayedResponse.responses().add(text2);

    return new SendWithDelayedResponse(compositeResponse, delayedResponse, Duration.ofSeconds(10));
  }
}
