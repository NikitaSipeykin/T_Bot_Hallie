package app.bot.config.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class HuggingFaceConfig {

  private final HuggingFaceProperties props;

  @Bean
  public WebClient huggingFaceWebClient() {
    return WebClient.builder()
        .baseUrl(props.getBaseUrl())
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getKey())
        .build();
  }
}

