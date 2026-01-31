package app.bot.handler.message.ai;

import app.bot.ai.OpenAIService;
import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.handler.message.MessageHandler;
import app.bot.state.UserState;
import app.module.node.texts.BotTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@RequiredArgsConstructor
public class OpenAiMessageHandler implements MessageHandler {

  private final BotTextService textService;
  private final OpenAIService aiService;

  @Override
  public UserState supports() {
    return UserState.OPEN_AI;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String prompt = message.getText();
    String response = aiService.askAI(prompt);

    return new TextResponse(chatId, response, null);
  }
}
