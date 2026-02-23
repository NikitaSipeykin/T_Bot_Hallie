package app.bot.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "huggingface.api")
@Data
public class HuggingFaceProperties {

  private String baseUrl;
  private String key;
  private String model;

  private int maxNewTokens;
  private double temperature;
  private int timeoutSeconds;
}

