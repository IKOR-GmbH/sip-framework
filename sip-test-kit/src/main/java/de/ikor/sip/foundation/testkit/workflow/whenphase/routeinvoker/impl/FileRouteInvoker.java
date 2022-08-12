package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.headers.FileExchangeHeaders.*;

import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.file.FileConsumer;
import org.apache.camel.component.file.FileEndpoint;
import org.apache.camel.support.EmptyAsyncCallback;
import org.springframework.stereotype.Component;

/** Invoker class for triggering routes with File consumer */
@Component
@RequiredArgsConstructor
@Slf4j
public class FileRouteInvoker implements RouteInvoker {

  private final CamelContext camelContext;

  @Override
  public Exchange invoke(Exchange inputExchange, Endpoint endpoint) {
    Route route =
        camelContext.getRoute(
            (String) inputExchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY));
    FileConsumer fileConsumer = (FileConsumer) route.getConsumer();

    Exchange fileExchange = createFileExchange(fileConsumer, inputExchange);

    fileConsumer.getAsyncProcessor().process(fileExchange, EmptyAsyncCallback.get());

    return createEmptyExchange();
  }

  @Override
  public boolean isApplicable(Endpoint endpoint) {
    return endpoint instanceof FileEndpoint;
  }

  @Override
  public boolean isSuspendable() {
    return true;
  }

  private Exchange createFileExchange(FileConsumer fileConsumer, Exchange inputExchange) {
    Exchange fileExchange = fileConsumer.createExchange(true);
    fileExchange.getMessage().setBody(inputExchange.getMessage().getBody());
    fileExchange.getMessage().setHeaders(prepareFileHeaders(inputExchange));
    return fileExchange;
  }

  private Map<String, Object> prepareFileHeaders(Exchange inputExchange) {
    Map<String, Object> headers = inputExchange.getMessage().getHeaders();

    prepareDefaultHeaders(headers, inputExchange.getMessage().getBody(String.class));
    if (headers.containsKey(CAMEL_FILE_NAME.getValue())) {
      prepareHeadersWithFilename(headers);
    }
    prepareOtherHeaders(headers);

    return headers;
  }

  private void prepareDefaultHeaders(Map<String, Object> headers, String bodyPayload) {
    if (!headers.containsKey(CAMEL_FILE_LENGTH.getValue())) {
      headers.put(CAMEL_FILE_LENGTH.getValue(), (long) bodyPayload.length());
    }
  }

  private void prepareHeadersWithFilename(Map<String, Object> headers) {
    String filename = (String) headers.get(CAMEL_FILE_NAME.getValue());
    if (!headers.containsKey(CAMEL_FILE_NAME_CONSUMED.getValue())) {
      headers.put(CAMEL_FILE_NAME_CONSUMED.getValue(), filename);
    }
    if (!headers.containsKey(CAMEL_FILE_NAME_ONLY.getValue())) {
      headers.put(CAMEL_FILE_NAME_ONLY.getValue(), filename);
    }
  }

  private void prepareOtherHeaders(Map<String, Object> headers) {
    if (headers.containsKey(CAMEL_FILE_LAST_MODIFIED.getValue())) {
      Long lastModifiedTimestamp = (Long) headers.get(CAMEL_FILE_LAST_MODIFIED.getValue());
      if (!headers.containsKey(CAMEL_MESSAGE_TIMESTAMP.getValue())) {
        headers.put(CAMEL_MESSAGE_TIMESTAMP.getValue(), lastModifiedTimestamp);
      }
    }
  }

  private Exchange createEmptyExchange() {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }
}
