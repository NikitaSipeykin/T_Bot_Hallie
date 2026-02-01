package app.bot.handler.message.ai;

import app.bot.ai.OpenRouterAIService;
import app.bot.ai.dto.ChatCompletionRequest;
import app.bot.ai.dto.ChatMessage;
import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.config.ai.OpenRouterProperties;
import app.bot.handler.message.MessageHandler;
import app.bot.state.UserState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenRouterMessageHandler implements MessageHandler {

  private final OpenRouterAIService aiService;
  private final OpenRouterProperties props;

  @Override
  public UserState supports() {
    return UserState.OPENROUTER_AI;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    ChatMessage userMessage = new ChatMessage("user", message.getText());

    ChatCompletionRequest request = new ChatCompletionRequest(props.getModel(), List.of(userMessage), 7d, 20);

    String json = "  \"model\": \"" + props.getModel() + "\",\n" +
                  "  \"messages\": [\n" +
                  "    {\n" +
                  "      \"role\": \"user\",\n" +
                  "      \"content\": \"" + message.getText() + "\"\n" +
                  "    }\n" +
                  "  ],\n" +
                  "  \"temperature\": 0.7,\n" +
                  "  \"max_tokens\": 200\n" +
                  "}\n";

    String answer = aiService.ask(json);
    return new TextResponse(chatId, answer, null);
  }
}
