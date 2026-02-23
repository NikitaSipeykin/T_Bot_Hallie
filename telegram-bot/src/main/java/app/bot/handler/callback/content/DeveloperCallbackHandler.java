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
public class DeveloperCallbackHandler  implements CallbackHandler {

  private final BotTextService textService;
  private final AccessServiceImpl accessService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.WEB_PANEL_TEXT_BUTTON);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.STATES);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    MediaResponse audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.AUDIO_DEVELOPER_INTRO);

    TextResponse text = new TextResponse(chatId, textService.format(TextMarker.DEVELOPER_TEXT),
        KeyboardFactory.from(List.of(
             KeyboardOption.callback(textService.format(TextMarker.DEVELOPER_TEXT_BUTTON), TextMarker.DEVELOPER_TEXT_BUTTON))));

    compositeResponse.responses().add(audio);
    compositeResponse.responses().add(text);

    audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.AUDIO_DEVELOPER_END);

    compositeResponse.responses().add(audio);

    return compositeResponse;
  }
}
