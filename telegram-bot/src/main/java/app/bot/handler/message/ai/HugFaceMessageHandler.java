package app.bot.handler.message.ai;

import app.bot.ai.HuggingFaceAIService;
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
public class HugFaceMessageHandler  implements MessageHandler {

  private final BotTextService textService;
  private final HuggingFaceAIService huggingFaceAIService;

  @Override
  public UserState supports() {
    return UserState.HUG_FACE_AI;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String prompt = message.getText();
    String answer = huggingFaceAIService.ask(prompt);

    return new TextResponse(chatId, answer, null);
  }
}
