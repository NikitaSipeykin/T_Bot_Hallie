package app.bot.handler.callback.content;

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
public class SchedulerCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final AccessServiceImpl accessService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.SCHEDULER_BUTTON);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.STATES);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());
    CompositeResponse delayedResponse = new CompositeResponse(new ArrayList<>());
    MediaResponse audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.SCHEDULER_INTRO);

    TextResponse text;

    compositeResponse.responses().add(audio);

    text = new TextResponse(chatId, textService.format(TextMarker.SCHEDULER_TEXT), null);

    audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.SCHEDULER_OUTRO);

    delayedResponse.responses().add(audio);
    delayedResponse.responses().add(text);

    text = new TextResponse(chatId, textService.format(TextMarker.SCHEDULER_NEXT),
        KeyboardFactory.from(List.of(
             KeyboardOption.callback(textService.format(TextMarker.SCHEDULER_NEXT_BUTTON), TextMarker.SCHEDULER_NEXT_BUTTON))));

    delayedResponse.responses().add(text);

    return new SendWithDelayedResponse(compositeResponse, delayedResponse, Duration.ofSeconds(10));
  }
}
