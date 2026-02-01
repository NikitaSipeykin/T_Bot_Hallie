package app.bot.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletionRequest {

  private String model;

  private List<ChatMessage> messages;

  private Double temperature;

  @JsonProperty("max_tokens")
  private Integer maxTokens;
}
