package de.ikor.sip.foundation.testkit.util;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.stream.Collectors;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/** Helper class with common methods for rest and soap invokers */
public class HttpInvokerHelper {

  /**
   * Creates headers for test kit execution
   *
   * @param exchange {@link Exchange} from which data is extracted
   * @return Map with test kit headers
   */
  public static MultiValueMap<String, String> prepareHeaders(Exchange exchange) {
    MultiValueMap<String, String> headers = new HttpHeaders();
    headers.add(
        RouteInvoker.TEST_NAME_HEADER,
        exchange.getMessage().getHeader(RouteInvoker.TEST_NAME_HEADER, String.class));
    headers.add(
        ProcessorProxy.TEST_MODE_HEADER,
        exchange.getMessage().getHeader(ProcessorProxy.TEST_MODE_HEADER, String.class));
    return headers;
  }

  /**
   * Create {@link Exchange} with a response recovered from test execution
   *
   * @param response {@link ResponseEntity}asd98 of test execution
   * @param camelContext {@link CamelContext}
   * @return {@link Exchange} with response
   */
  public static Exchange createExchangeResponse(
      ResponseEntity<String> response, CamelContext camelContext) {
    ExchangeBuilder exchangeBuilder =
        ExchangeBuilder.anExchange(camelContext).withBody(formatToOneLine(response.getBody()));
    response.getHeaders().forEach(exchangeBuilder::withHeader);
    return exchangeBuilder.build();
  }

  private static String formatToOneLine(String multilineString) {
    if (multilineString != null) {
      return multilineString.lines().map(String::strip).collect(Collectors.joining(""));
    }
    return null;
  }
}
