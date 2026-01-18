package app.bot.handler.callback.content;


import app.bot.bot.CommandKey;
import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.handler.command.CommandHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WhoIsHallieCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;

  @Override
  public boolean supports(String callbackData){
    return callbackData.equals(TextMarker.WHO_IS_HALLIE_BUTTON);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.HALLIE);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    //Todo: Add audio or voice. Text: [starts laughing] I’m here to make things easier.
    MediaResponse audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.HALLIE_WHO_AM_I);

    TextResponse text1 = new TextResponse(chatId, textService.format(TextMarker.HALLIE_INTRODUCTION), null);

    TextResponse text2 = new TextResponse(chatId, textService.format(TextMarker.HALLIE_INTRODUCTION_WANT_TO_SEE),
        KeyboardFactory.from(List.of(
            new KeyboardOption(textService.format(TextMarker.HALLIE_INTRODUCTION_BUTTON_YES), TextMarker.BOT_ABILITY_BUTTON),
            new KeyboardOption(textService.format(TextMarker.HALLIE_INTRODUCTION_BUTTON_BACK), TextMarker.HALLIE_INTRODUCTION_BUTTON_BACK))));

    compositeResponse.responses().add(audio);
    compositeResponse.responses().add(text1);
    compositeResponse.responses().add(text2);

    return compositeResponse;
  }
}