package app.web.transcription;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TranscriptionPageController {

  @GetMapping("/transcription")
  public String transcriptionPage() {
    return "transcription.html";
  }
}
