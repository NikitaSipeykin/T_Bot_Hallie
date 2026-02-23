package app.bot.config.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class OpenRouterConfig {

  private final OpenRouterProperties props;

  @Bean
  public WebClient openRouterWebClient() {
    return WebClient.builder()
        .baseUrl(props.getBaseUrl())
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getKey())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        // обязательные хедеры OpenRouter
        .defaultHeader("HTTP-Referer", "https://telegram-bot")
        .defaultHeader("X-Title", "Telegram Bot AI")
        .build();
  }
}
