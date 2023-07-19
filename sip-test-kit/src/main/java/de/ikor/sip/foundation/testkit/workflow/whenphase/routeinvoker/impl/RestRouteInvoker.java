package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.testkit.util.TestKitHelper;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.rest.RestEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

/** Invoker class for triggering Camel REST route */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestRouteInvoker implements RouteInvoker {

  private final CamelContext camelContext;
  private final Environment environment;
  private final RestTemplateBuilder restTemplateBuilder;

  @Value("${sip.adapter.camel-endpoint-context-path}")
  private String contextPath = "";

  @Override
  public Optional<Exchange> invoke(Exchange inputExchange) {
    Endpoint endpoint = TestKitHelper.resolveEndpoint(inputExchange, camelContext);
    HttpEntity<String> request =
        new HttpEntity<>(
            inputExchange.getMessage().getBody(String.class), prepareHeaders(inputExchange));
    log.trace("sip.testkit.workflow.whenphase.routeinvoker.rest.request_{}", request);

    ResponseEntity<String> response =
        restTemplateBuilder
            .build()
            .exchange(
                createAddressUri(endpoint),
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {});
    log.trace("sip.testkit.workflow.whenphase.routeinvoker.soap.response_{}", response);

    return Optional.of(createExchangeResponse(response));
  }

  private MultiValueMap<String, String> prepareHeaders(Exchange exchange) {
    MultiValueMap<String, String> headers = new HttpHeaders();
    headers.add(
        RouteInvoker.TEST_NAME_HEADER,
        exchange.getMessage().getHeader(RouteInvoker.TEST_NAME_HEADER, String.class));
    headers.add(
        ProcessorProxy.TEST_MODE_HEADER,
        exchange.getMessage().getHeader(ProcessorProxy.TEST_MODE_HEADER, String.class));
    return headers;
  }

  @Override
  public boolean isApplicable(Endpoint endpoint) {
    return endpoint instanceof RestEndpoint;
  }

  private String createAddressUri(Endpoint endpoint) {
    return String.format(
        "http://localhost:%s%s/%s",
        environment.getProperty("local.server.port"),
        resolveContextPath(),
        ((RestEndpoint) endpoint).getPath());
  }

  private String resolveContextPath() {
    return contextPath.replaceAll("/[*]$", "");
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
    log.trace("sip.testkit.workflow.whenphase.routeinvoker.rest.responseformating");
    return null;
  }
}
