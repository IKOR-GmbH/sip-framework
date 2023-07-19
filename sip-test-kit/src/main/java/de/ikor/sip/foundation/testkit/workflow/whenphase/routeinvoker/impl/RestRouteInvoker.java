package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static de.ikor.sip.foundation.testkit.util.HttpInvokerHelper.createExchangeResponse;
import static de.ikor.sip.foundation.testkit.util.HttpInvokerHelper.prepareHeaders;

import de.ikor.sip.foundation.testkit.util.TestKitHelper;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.component.rest.RestEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
    log.trace("sip.testkit.workflow.whenphase.routeinvoker.rest.response_{}", response);

    return Optional.of(createExchangeResponse(response, camelContext));
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
}
