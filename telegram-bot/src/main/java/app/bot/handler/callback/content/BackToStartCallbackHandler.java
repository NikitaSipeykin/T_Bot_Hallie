package app.bot.handler.callback.content;

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

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BackToStartCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.HALLIE_INTRODUCTION_BUTTON_BACK);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    String firstName = query.getFrom().getFirstName();

    userStateService.setState(chatId, UserState.DEFAULT);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    TextResponse text = new TextResponse(chatId,
        textService.format(
            TextMarker.START,
            firstName != null ? firstName : textService.format(TextMarker.START_COMMAND_USERNAME_FILLER)),
        KeyboardFactory.from(List.of(
            new KeyboardOption(textService.format(TextMarker.BOT_ABILITY_BUTTON), TextMarker.BOT_ABILITY_BUTTON),
            new KeyboardOption(textService.format(TextMarker.DEMO_MODE_BUTTON), TextMarker.DEMO_MODE_BUTTON),
            new KeyboardOption(textService.format(TextMarker.WHO_IS_HALLIE_BUTTON), TextMarker.WHO_IS_HALLIE_BUTTON))));

    compositeResponse.responses().add(text);

    return compositeResponse;
  }
}
