package app.bot.handler.message.ai;

import app.bot.ai.AIService;
import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.message.MessageHandler;
import app.bot.state.UserState;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class AiMessageHandler implements MessageHandler {

  private final BotTextService textService;
  private final AIService aiService;

  @Override
  public UserState supports() {
    return UserState.AI;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String prompt = message.getText();
    String response = aiService.askAI(prompt);

    return new TextResponse(chatId, response, null);
  }
}
