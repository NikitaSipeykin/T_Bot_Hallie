package app.bot.handler.callback.content.demo;

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
public class BeforeDemoCallbackHandler implements CallbackHandler {

  private final BotTextService textService;
  private final AccessServiceImpl accessService;
  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;


  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals(TextMarker.BOT_ABILITY_BUTTON_NEED_BOT) ||
           callbackData.equals(TextMarker.BOT_ABILITY_BUTTON_CURIOUS) ||
           callbackData.equals(TextMarker.BOT_ABILITY_BUTTON_DEV);
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    String callbackData = query.getData();
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.DEMO);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());
    TextResponse text1 = null;
    MediaResponse audio = null;

    if (callbackData.equals(TextMarker.BOT_ABILITY_BUTTON_NEED_BOT)){
      accessService.grantGoal(chatId, textService.format(TextMarker.BOT_ABILITY_BUTTON_NEED_BOT));
      audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.BEFORE_DEMO_NEED_BOT);
      text1 = new TextResponse(chatId, textService.format(TextMarker.BEFORE_DEMO_NEED_BOT), null);
    }

    if (callbackData.equals(TextMarker.BOT_ABILITY_BUTTON_CURIOUS)){
      accessService.grantGoal(chatId, textService.format(TextMarker.BOT_ABILITY_BUTTON_CURIOUS));
      audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.BEFORE_DEMO_CURIOUS);
      text1 = new TextResponse(chatId, textService.format(TextMarker.BEFORE_DEMO_CURIOUS), null);
    }

    if (callbackData.equals(TextMarker.BOT_ABILITY_BUTTON_DEV)){
      accessService.grantGoal(chatId, "I am interested in development");
      audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.BEFORE_DEMO_DEV);
      text1 = new TextResponse(chatId, textService.format(TextMarker.BEFORE_DEMO_DEV), null);
    }

    TextResponse text2 = new TextResponse(chatId, textService.format(TextMarker.BEFORE_DEMO_BEGIN),
        KeyboardFactory.from(List.of(
            new KeyboardOption(textService.format(TextMarker.BEFORE_DEMO_BEGIN_BUTTON), TextMarker.BEFORE_DEMO_BEGIN_BUTTON))));

    compositeResponse.responses().add(audio);
    compositeResponse.responses().add(text1);
    compositeResponse.responses().add(text2);

    return compositeResponse;
  }
}
