package app.bot.handler.command;

import app.bot.bot.CommandKey;
import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.config.BotProperties;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.broadcast.BroadcastService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiCommandHandler implements CommandHandler {

  private final UserStateService userStateService;
  private final BotTextService textService;

  @Override
  public String command() {
    return CommandKey.AI;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();

    userStateService.setState(chatId, UserState.AI);
    return new TextResponse(chatId, "Next message will send to ai!", null);
  }
}

