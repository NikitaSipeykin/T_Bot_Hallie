package app.bot.ai;

import app.bot.ai.dto.ChatCompletionRequest;
import app.bot.ai.dto.ChatCompletionResponse;
import app.bot.ai.dto.ChatMessage;
import app.bot.config.ai.OpenRouterProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenRouterAIService implements AIService {

  private final WebClient openRouterWebClient;
  private final OpenRouterProperties props;

  @Override
  public String ask(String prompt) {

    ChatCompletionRequest request = ChatCompletionRequest.builder()
        .model(props.getModel())
        .messages(List.of(
            new ChatMessage("user", prompt)
        ))
        .temperature(props.getTemperature())
        .maxTokens(props.getMaxTokens())
        .build();

    return ask(request);
  }

  public String ask(ChatCompletionRequest request) {
    try {
      return openRouterWebClient.post()
          .uri("/chat/completions")
          .bodyValue(request)
          .retrieve()
          .bodyToMono(ChatCompletionResponse.class)
          .timeout(Duration.ofSeconds(props.getTimeoutSeconds()))
          .map(response ->
              response.getChoices().get(0).getMessage().getContent()
          )
          .block();

    } catch (Exception e) {
      log.error("OpenRouter AI error", e);
      return "🤖 I can't answer right now, please try later.";
    }
  }
}
