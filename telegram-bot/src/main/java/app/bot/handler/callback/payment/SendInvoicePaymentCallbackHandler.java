package app.bot.handler.callback.payment;

import app.bot.bot.responce.BotResponse;
import app.bot.bot.responce.CompositeResponse;
import app.bot.bot.responce.SendInvoiceResponse;
import app.bot.bot.responce.TextResponse;
import app.bot.facade.AnalyticsFacade;
import app.bot.handler.callback.CallbackHandler;
import app.bot.keyboard.KeyboardFactory;
import app.bot.keyboard.KeyboardOption;
import app.bot.state.UserState;
import app.bot.state.UserStateService;
import app.module.converter.ExchangeRateServiceImpl;
import app.module.node.texts.BotTextService;
import app.module.node.texts.TextMarker;
import app.module.payment.PaymentService;
import app.module.payment.props.PaymentOption;
import app.module.payment.props.PaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SendInvoicePaymentCallbackHandler implements CallbackHandler {

  private final UserStateService userStateService;
  private final PaymentProperties paymentProperties;
  private final PaymentService paymentService;
  private final ExchangeRateServiceImpl rateService;
  private final AnalyticsFacade analytics;
  private final BotTextService textService;

  @Override
  public boolean supports(String callbackData) {
    return callbackData.equals("UZS") ||
           callbackData.equals("USD") ||
           callbackData.equals("EUR") ||
           callbackData.equals("RUB");
  }

  @Override
  public BotResponse handle(CallbackQuery query) {
    String currency = query.getData();
    Long chatId = query.getMessage().getChatId();
    userStateService.setState(chatId, UserState.PAYMENT);
    analytics.trackButtonClick(query, "CURRENCY_" + currency);

    try {
      PaymentOption option = paymentProperties.resolve(currency);
      String providerToken = option.providerToken();
      int price = option.price();
      int amount;

      String title = textService.format(TextMarker.SEND_INVOICE_TITLE);
      String description = textService.format(TextMarker.SEND_INVOICE_DESCRIPTION);
      String label = textService.format(TextMarker.SEND_INVOICE_LABEL);

      if (!currency.equals("USD")) {
        amount = rateService.convertFromUsd(price, currency);
      } else amount = price;

      String payload = "program_access_" + chatId;

      analytics.trackPaymentStart(chatId, payload, amount, currency);

      paymentService.createPayment(chatId, payload, amount, currency);

      SendInvoice invoice = SendInvoice.builder()
          .chatId(chatId.toString())
          .title(title)
          .description(description)
          .payload(payload)
          .providerToken(providerToken)
          .currency(currency)
          .prices(List.of(
              new LabeledPrice(label, amount)
          ))
          .startParameter("start")
          .build();

      SendInvoiceResponse invoiceResponse = new SendInvoiceResponse(invoice);

      CompositeResponse compositeResponse = new CompositeResponse(new ArrayList<>());

      TextResponse response = new TextResponse(chatId, textService.format(TextMarker.CURRENCY_CHOOSE_ANOTHER),
          KeyboardFactory.from(List.of(
               KeyboardOption.callback(textService.format(TextMarker.CURRENCY_CHOOSE_BUTTON_BACK), TextMarker.PAYMENT))));

      compositeResponse.responses().add(invoiceResponse);
      compositeResponse.responses().add(response);
      analytics.trackInvoiceShown(chatId, payload, currency);

      return compositeResponse;
    } catch (IllegalStateException e) {
      analytics.trackPaymentUnavailable(chatId, currency, e.getMessage());
      return new TextResponse(chatId, textService.format(TextMarker.CURRENCY_ERROR), null);
    }
  }

}
