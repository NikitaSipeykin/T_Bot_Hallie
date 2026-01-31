package app.bot.ai;

import app.bot.config.ai.HuggingFaceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HuggingFaceAIService {

  private final WebClient huggingFaceWebClient;
  private final HuggingFaceProperties props;

  public String ask(String prompt) {
    Map<String, Object> body = Map.of(
        "inputs", prompt,
        "parameters", Map.of(
            "max_new_tokens", props.getMaxNewTokens(),
            "temperature", props.getTemperature()
        )
    );

    try {
      return huggingFaceWebClient.post()
          .uri("/hf-inference/models/" + props.getModel())
          .bodyValue(body)
          .retrieve()
          .bodyToMono(List.class)
          .timeout(Duration.ofSeconds(props.getTimeoutSeconds()))
          .map(resp -> {
            Map<?, ?> item = (Map<?, ?>) resp.get(0);
            return item.get("generated_text").toString();
          })
          .block();

    } catch (Exception e) {
      log.error("HuggingFace AI error", e);
      return "🤖 I can't answer right now, please try later.";
    }
  }
}

