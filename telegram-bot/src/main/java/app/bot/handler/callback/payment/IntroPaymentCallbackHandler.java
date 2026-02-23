package app.bot.handler.callback.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
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

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IntroPaymentCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.PROJECT_DESCRIPTION);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();

    analytics.trackButtonClick(query, TextMarker.PROJECT_DESCRIPTION);

    analytics.trackBlockView(chatId, TextMarker.PROJECT_DESCRIPTION,
        Map.of("state", UserState.NEED_PAYMENT.name(), "source", "callback"));

    userStateService.setState(chatId, UserState.NEED_PAYMENT);

    return new TextResponse(chatId, textService.get(TextMarker.PROJECT_DESCRIPTION),
        KeyboardFactory.from(List.of(
             KeyboardOption.callback(textService.format(TextMarker.PROJECT_DESCRIPTION_BUTTON_YES), TextMarker.PAYMENT),
             KeyboardOption.callback(textService.format(TextMarker.PROJECT_DESCRIPTION_BUTTON_INFO), TextMarker.PROJECT_INFO))));
  }
}
