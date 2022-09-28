package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static de.ikor.sip.foundation.testkit.util.TestKitHelper.resolveConsumer;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.*;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.component.mail.MailConsumer;
import org.apache.camel.component.mail.MailEndpoint;
import org.apache.camel.component.mail.MailMessage;
import org.apache.camel.support.EmptyAsyncCallback;
import org.springframework.stereotype.Component;

/** Invoker of exchange processing for {@link MailConsumer} */
@Slf4j
@Component
@RequiredArgsConstructor
public class MailRouteInvoker implements RouteInvoker {

  private final CamelContext camelContext;

  @Override
  public Optional<Exchange> invoke(Exchange exchange) {
    MailConsumer mailConsumer = (MailConsumer) resolveConsumer(exchange, camelContext);
    Exchange mailExchange = createAndPopulateExchangeWithMailMessage(exchange, mailConsumer);
    mailConsumer.getAsyncProcessor().process(mailExchange, EmptyAsyncCallback.get());
    return Optional.empty();
  }

  @Override
  public boolean isApplicable(Endpoint endpoint) {
    return endpoint instanceof MailEndpoint;
  }

  private Exchange createAndPopulateExchangeWithMailMessage(
      Exchange exchange, MailConsumer mailConsumer) {
    Exchange mailExchange = mailConsumer.createExchange(true);
    mailExchange.setMessage(createMailMessage(exchange));
    return mailExchange;
  }

  private MailMessage createMailMessage(Exchange exchange) {
    MailMessage mailMessage = new MailMessage(exchange, createMimeMessage(exchange), true);
    mailMessage.setBody(exchange.getMessage().getBody());
    mailMessage.setHeaders(exchange.getMessage().getHeaders());
    return mailMessage;
  }

  private MimeMessage createMimeMessage(Exchange exchange) {
    MimeMessage message = new MimeMessage((Session) null);
    setMimeMessageHeaders(message, exchange.getMessage().getHeaders());
    setMimeMessageBody(message, exchange.getMessage().getBody(String.class));
    return message;
  }

  private void setMimeMessageHeaders(MimeMessage message, Map<String, Object> headers) {
    if (headers != null) {
      headers.forEach(
          (key, value) -> {
            try {
              message.setHeader(key, String.valueOf(value));
            } catch (MessagingException e) {
              log.error(e.getMessage());
            }
          });
    }
  }

  private void setMimeMessageBody(MimeMessage message, String body) {
    try {
      message.setText(body);
    } catch (MessagingException e) {
      log.error(e.getMessage());
    }
  }
}
