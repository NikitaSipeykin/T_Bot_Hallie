package app.bot.handler.message.mail;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.email.EmailService;
import app.bot.handler.message.MessageHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.core.broadcast.SubscriberService;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MailRequestMessageHandler implements MessageHandler {

  private final EmailService emailService;
  private final BotTextService textService;
  private final SubscriberService subscriberService;
  private final UserStateService userStateService;

  @Override
  public UserState supports() {
    return UserState.MAIL_REQUEST;
  }

  @Override
  public BotResponse handle(Message message) {
    Long chatId = message.getChatId();
    String email = message.getText();
    String name = message.getFrom().getFirstName();

    if (!emailService.isValidEmail(email)) {
      return new TextResponse(chatId, textService.format(TextMarker.NOT_EMAIL), null);
    }

//    String code = emailService.generateCode();
    subscriberService.setEmail(chatId, email);
//    subscriberService.setCode(chatId, code);

//    emailService.sendEmail(email);
    emailService.sendHallieDemoEmail(email, name);
    userStateService.setState(chatId, UserState.WAIT_EMAIL);

    return new TextResponse(chatId, textService.format(TextMarker.SEND_EMAIL),
        KeyboardFactory.from(List.of(
            new KeyboardOption(textService.format(TextMarker.EMAIL_WAS_SENT_BUTTON), TextMarker.EMAIL_WAS_SENT_BUTTON))));
  }
}
