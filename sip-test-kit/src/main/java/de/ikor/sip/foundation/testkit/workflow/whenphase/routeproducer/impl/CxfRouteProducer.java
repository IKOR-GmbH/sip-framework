package de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.RouteProducer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/** Class for triggering Camel CXF(SOAP) route */
@Component
@RequiredArgsConstructor
@Slf4j
public class CxfRouteProducer implements RouteProducer {

  private static final String TEST_MODE_HEADER_KEY = "test-mode";
  private static final String TEST_NAME_HEADER_KEY = "test-name";

  private final CamelContext camelContext;
  private final Environment environment;
  private final RestTemplate restTemplate;

  @Value("${sip.adapter.camel-cxf-endpoint-context-path}")
  private String cxfContextPath = "";

  @Override
  public Exchange executeTask(Exchange exchange, Endpoint endpoint) {
    MultiValueMap<String, String> headers = new HttpHeaders();
    headers.add(
        TEST_MODE_HEADER_KEY, exchange.getMessage().getHeader(TEST_MODE_HEADER_KEY, String.class));
    headers.add(
        TEST_NAME_HEADER_KEY, exchange.getMessage().getHeader(TEST_NAME_HEADER_KEY, String.class));
    HttpEntity<String> request =
        new HttpEntity<>(exchange.getMessage().getBody(String.class), headers);
    log.trace("SIP Test Kit send request: {}", request);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createAddressUri(endpoint),
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<>() {});
    log.trace("SIP Test Kit receives response: {}", response);

    return createExchangeResponse(response);
  }

  private Exchange createExchangeResponse(ResponseEntity<String> response) {
    ExchangeBuilder exchangeBuilder =
        ExchangeBuilder.anExchange(camelContext).withBody(formatToOneLine(response.getBody()));
    response.getHeaders().forEach(exchangeBuilder::withHeader);
    return exchangeBuilder.build();
  }

  private String formatToOneLine(String multilineString) {
    if (multilineString != null) {
      return multilineString.lines().map(String::strip).collect(Collectors.joining(""));
    }
    log.trace("SIP Test Kit response has no body");
    return null;
  }

  private String createAddressUri(Endpoint endpoint) {
    return String.format(
        "http://localhost:%s%s/%s",
        environment.getProperty("local.server.port"),
        trimAddressSuffix(cxfContextPath),
        trimAddressPrefix(getCxfEndpointAddress(endpoint)));
  }

  private String trimAddressSuffix(String address) {
    return address.replaceAll("/$", "");
  }

  private String trimAddressPrefix(String address) {
    return address.replaceFirst("/", "");
  }

  String getCxfEndpointAddress(Endpoint endpoint) {
    return ((CxfEndpoint) endpoint).getAddress();
  }
}
