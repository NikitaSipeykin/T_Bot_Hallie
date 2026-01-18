package app.bot.handler.command;

import app.bot.bot.CommandKey;
import app.bot.bot.responce.*;
import app.bot.facade.AnalyticsFacade;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;


@Component
@RequiredArgsConstructor
public class AdminQuestionCommandHandler implements CommandHandler {

  private final UserStateService userStateService;
  private final AnalyticsFacade analytics;
  private final BotTextService textService;

  @Override
  public String command() {
    return CommandKey.REQUEST;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();

      userStateService.setState(chatId, UserState.REQUEST);
      return new TextResponse(chatId, textService.format(TextMarker.ADMIN_QUESTION_USER_WARN), null);
  }
}

