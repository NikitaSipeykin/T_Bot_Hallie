package app.bot.bot;

import app.bot.bot.responce.BotResponseProcessor;
import app.bot.config.BotProperties;
import app.bot.dispatcher.CallbackDispatcher;
import app.bot.dispatcher.CommandDispatcher;
import app.bot.dispatcher.MessageDispatcher;
import app.bot.sender.TelegramSender;
import app.bot.state.UserStateService;
import app.core.payment.PaymentCommand;
import app.module.node.texts.BotTextService;
import app.module.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class DemoBot extends BaseTelegramBot {

  private final PaymentService paymentService;
  private final UserStateService userStateService;

  private final BotTextService text;


  public DemoBot(
      BotProperties botProperties, CallbackDispatcher callbackDispatcher, CommandDispatcher commandDispatcher,
      MessageDispatcher messageDispatcher, PaymentService paymentService, UserStateService userStateService,
      TelegramSender telegramSender,  BotTextService text, BotResponseProcessor botResponseProcessor) {
    super(botProperties, callbackDispatcher, commandDispatcher, messageDispatcher, botResponseProcessor, telegramSender);
    this.paymentService = paymentService;
    this.userStateService = userStateService;
    this.text = text;
  }

  // ================= PAYMENTS =================

  @Override
  protected void handlePreCheckout(Update update) {
    var query = update.getPreCheckoutQuery();

    AnswerPreCheckoutQuery answer = AnswerPreCheckoutQuery.builder()
        .preCheckoutQueryId(query.getId())
        .ok(true)
        .build();

    executeSafely(answer);
  }

  @Override
  protected void handleSuccessfulPayment(Update update) {
    var message = update.getMessage();
    var payment = message.getSuccessfulPayment();

    PaymentCommand command = new PaymentCommand(
        message.getFrom().getId(),
        message.getChatId(),
        payment.getInvoicePayload(),
        payment.getTotalAmount(),
        payment.getCurrency(),
        payment.getTelegramPaymentChargeId()
    );

    paymentService.handlePayment(command);
  }

  // ================= OPTIONAL =================

  @Override
  protected void handleUnknown(Update update) {
    log.warn("Unhandled update: {}", update);
  }

}
