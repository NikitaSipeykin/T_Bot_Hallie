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
public class DemoNoAccessCallbackHandler  implements CallbackHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.DEMO_ACCESS_BUTTON_CONTINUE);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.DEMO_ACCESS);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    TextResponse text = new TextResponse(chatId, textService.format(TextMarker.NO_ACCESS),
        KeyboardFactory.from(List.of(
            new KeyboardOption(textService.format(TextMarker.ACCESS_BUTTON_NEXT), TextMarker.EMAIL))));

    compositeResponse.responses().add(text);

    return compositeResponse;
  }
}
