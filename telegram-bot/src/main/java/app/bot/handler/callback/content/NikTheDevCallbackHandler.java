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
public class NikTheDevCallbackHandler  implements CallbackHandler {

  private final BotTextService textService;
  private final AccessServiceImpl accessService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.DEVELOPER_TEXT_BUTTON);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.STATES);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    MediaResponse audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.AUDIO_FINAL);
    TextResponse text = new TextResponse(chatId, textService.format(TextMarker.NIK_THE_DEV_TEXT),
        KeyboardFactory.from(List.of(
             KeyboardOption.callback(textService.format(TextMarker.ORDER_BOT_BUTTON), TextMarker.ORDER_BOT_BUTTON),
             KeyboardOption.callback(textService.format(TextMarker.WRITE_TO_DEV_BUTTON), TextMarker.WRITE_TO_DEV_BUTTON),
             KeyboardOption.url(textService.format(TextMarker.GITHUB_BUTTON), "https://github.com/NikitaSipeykin"),
             KeyboardOption.callback(textService.format(TextMarker.AI_BUTTON), TextMarker.AI_BUTTON))));

    compositeResponse.responses().add(audio);
    compositeResponse.responses().add(text);

    return compositeResponse;
  }
}
