package app.bot.handler.callback.content.email;

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
public class EmailCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.EMAIL);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.EMAIL);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    MediaResponse audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.EMAIL_AUDIO);

    TextResponse text = new TextResponse(chatId, textService.format(TextMarker.EMAIL),
        KeyboardFactory.from(List.of(
            new KeyboardOption(textService.format(TextMarker.SEND_EMAIL_BUTTON), TextMarker.SEND_EMAIL_BUTTON),
            new KeyboardOption(textService.format(TextMarker.NO_EMAIL_BUTTON), TextMarker.NO_EMAIL_BUTTON))));

    compositeResponse.responses().add(audio);
    compositeResponse.responses().add(text);

    return compositeResponse;
  }
}
