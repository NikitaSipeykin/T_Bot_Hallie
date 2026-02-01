package app.bot.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "openrouter.api")
public class OpenRouterProperties {

  private String baseUrl;
  private String key;
  private String model;

  private double temperature;
  private int maxTokens;
  private int timeoutSeconds;
}
