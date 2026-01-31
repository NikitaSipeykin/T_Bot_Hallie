package app.bot.ai;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAIService {
  private final OpenAiService openAiService;

  public OpenAIService(@Value("${openai.api.key}") String apiKey) {
    this.openAiService = new OpenAiService(apiKey);
  }

  public String askAI(String prompt) {
    ChatCompletionRequest request = ChatCompletionRequest.builder()
        .model("gpt-3.5-turbo")
        .messages(List.of(new ChatMessage("user", prompt)))
        .build();

    return openAiService.createChatCompletion(request)
        .getChoices()
        .get(0)
        .getMessage()
        .getContent();
  }
}
