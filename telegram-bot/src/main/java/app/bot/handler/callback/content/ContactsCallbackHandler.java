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

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContactsCallbackHandler  implements CallbackHandler {

  private final BotTextService textService;
  private final AccessServiceImpl accessService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.ORDER_BOT_BUTTON) ||
           callbackData.equals(TextMarker.WRITE_TO_DEV_BUTTON);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.STATES);

    return new TextResponse(chatId, textService.format(TextMarker.CONTACTS_TEXT),
        KeyboardFactory.from(List.of(
            KeyboardOption.callback(textService.format(TextMarker.HALLIE_INTRODUCTION_BUTTON_BACK), TextMarker.DEVELOPER_TEXT_BUTTON))));
  }
}
