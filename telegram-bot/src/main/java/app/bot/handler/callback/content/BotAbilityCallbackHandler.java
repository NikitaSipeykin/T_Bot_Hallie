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
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BotAbilityCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.BOT_ABILITY_BUTTON);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.BOT_ABILITY);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());
    MediaResponse audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.HALLIE_BOT_ABILITY);
    TextResponse text1 = new TextResponse(chatId, textService.format(TextMarker.BOT_ABILITY_INTRODUCTION), null);
    TextResponse text2 = new TextResponse(chatId, textService.format(TextMarker.BOT_ABILITY_ASK),
        KeyboardFactory.from(List.of(
             KeyboardOption.callback(textService.format(TextMarker.BOT_ABILITY_BUTTON_NEED_BOT), TextMarker.BOT_ABILITY_BUTTON_NEED_BOT),
             KeyboardOption.callback(textService.format(TextMarker.BOT_ABILITY_BUTTON_CURIOUS), TextMarker.BOT_ABILITY_BUTTON_CURIOUS),
             KeyboardOption.callback(textService.format(TextMarker.BOT_ABILITY_BUTTON_DEV), TextMarker.BOT_ABILITY_BUTTON_DEV))));

    compositeResponse.responses().add(audio);
    compositeResponse.responses().add(text1);
    compositeResponse.responses().add(text2);

    return compositeResponse;
  }
}
