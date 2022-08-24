package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.headers.FileExchangeHeaders.*;

import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
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
  public Optional<Exchange> invoke(Exchange inputExchange) {
    Route route =
        camelContext.getRoute(
            (String) inputExchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY));
    FileConsumer fileConsumer = (FileConsumer) route.getConsumer();

    Exchange fileExchange = fileConsumer.createExchange(true);
    prepareFileExchange(fileExchange, inputExchange);

    fileConsumer.getAsyncProcessor().process(fileExchange, EmptyAsyncCallback.get());
    return Optional.empty();
  }

  @Override
  public boolean isApplicable(Endpoint endpoint) {
    return endpoint instanceof FileEndpoint;
  }

  private void prepareFileExchange(Exchange fileExchange, Exchange inputExchange) {
    fileExchange.getMessage().setBody(inputExchange.getMessage().getBody());
    fileExchange.getMessage().setHeaders(prepareFileHeaders(inputExchange));
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
    headers.putIfAbsent(CAMEL_FILE_LENGTH.getValue(), (long) bodyPayload.length());
  }

  private void prepareHeadersWithFilename(Map<String, Object> headers) {
    String filename = (String) headers.get(CAMEL_FILE_NAME.getValue());
    headers.putIfAbsent(CAMEL_FILE_NAME_CONSUMED.getValue(), filename);
    headers.putIfAbsent(CAMEL_FILE_NAME_ONLY.getValue(), filename);
  }

  private void prepareOtherHeaders(Map<String, Object> headers) {
    if (headers.containsKey(CAMEL_FILE_LAST_MODIFIED.getValue())) {
      Long lastModifiedTimestamp = (Long) headers.get(CAMEL_FILE_LAST_MODIFIED.getValue());
      headers.putIfAbsent(CAMEL_MESSAGE_TIMESTAMP.getValue(), lastModifiedTimestamp);
    }
  }
}
