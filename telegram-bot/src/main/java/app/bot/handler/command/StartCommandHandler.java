package app.bot.handler.command;

import app.bot.bot.CommandKey;
import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.broadcast.SubscriberService;
import app.module.content.AccessServiceImpl;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartCommandHandler implements CommandHandler {

  private final BotTextService textService;
  private final UserStateService userStateService;
  private final AccessServiceImpl accessService;
  private final SubscriberService subscriberService;

  private final AnalyticsFacade analytics;

  @Override
  public String command() {
    return CommandKey.START;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String firstName = message.getFrom().getFirstName();
    String username = message.getFrom().getUserName();
    String language = message.getFrom().getLanguageCode();

    subscriberService.subscribe(chatId, username, firstName, language);
    userStateService.setState(chatId, UserState.DEFAULT);
    analytics.trackSubscribe(message, true);
    analytics.trackBlockView(chatId, "PROGRAM_CONTINUE");
    accessService.createIfNotExists(chatId);

    CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

    MediaResponse audio = new MediaResponse(chatId, MediaType.VOICE, CommandKey.HALLIE_START);

    TextResponse text = new TextResponse(chatId,
        textService.format(TextMarker.START,
            firstName != null ? firstName : textService.format(TextMarker.START_COMMAND_USERNAME_FILLER)),
        KeyboardFactory.from(List.of(
            KeyboardOption.callback(textService.format(TextMarker.BOT_ABILITY_BUTTON), TextMarker.BOT_ABILITY_BUTTON),
            KeyboardOption.callback(textService.format(TextMarker.DEMO_MODE_BUTTON), TextMarker.DEMO_MODE_BUTTON),
            KeyboardOption.callback(textService.format(TextMarker.WHO_IS_HALLIE_BUTTON), TextMarker.WHO_IS_HALLIE_BUTTON))));

    compositeResponse.responses().add(audio);
    compositeResponse.responses().add(text);

    return compositeResponse;
  }
}

