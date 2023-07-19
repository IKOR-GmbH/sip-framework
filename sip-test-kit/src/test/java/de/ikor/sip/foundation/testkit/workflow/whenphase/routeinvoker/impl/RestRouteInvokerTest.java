package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.Route;
import org.apache.camel.component.rest.RestEndpoint;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

class RestRouteInvokerTest {

  private static final String ROUTE_ID = "routeId";
  public static final String TEST_RESPONSE = "test response";

  @Test
  void GIVEN_mockedExchangeAndEndpoint_WHEN_executeTask_THEN_verifySendingToGoodEndpointUri() {
    // arrange
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
    RestTemplate restTemplate = mock(RestTemplate.class);
    RestRouteInvoker subject =
        new RestRouteInvoker(camelContext, mock(Environment.class), restTemplateBuilder);
    RestEndpoint restEndpoint = mock(RestEndpoint.class);
    Exchange exchange = mock(Exchange.class, RETURNS_DEEP_STUBS);
    ResponseEntity<String> routeExpectedResponse =
        new ResponseEntity<>(TEST_RESPONSE, HttpStatus.OK);
    when(restTemplateBuilder.build()).thenReturn(restTemplate);
    when(restEndpoint.getMethod()).thenReturn("post");
    when(restEndpoint.getPath()).thenReturn("test");
    when(restTemplate.exchange(
            anyString(),
            any(HttpMethod.class),
            any(),
            ArgumentMatchers.<ParameterizedTypeReference>any()))
        .thenReturn(routeExpectedResponse);

    when(exchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY)).thenReturn(ROUTE_ID);
    when(exchange.getMessage().getBody(String.class)).thenReturn("request");
    when(exchange.getMessage().getHeader(RouteInvoker.TEST_NAME_HEADER, String.class))
        .thenReturn("test");
    when(exchange.getMessage().getHeader(ProcessorProxy.TEST_MODE_HEADER, String.class))
        .thenReturn("true");
    Route route = mock(Route.class);
    when(camelContext.getRoute(ROUTE_ID)).thenReturn(route);
    when(route.getEndpoint()).thenReturn(restEndpoint);

    // act
    Optional<Exchange> target = subject.invoke(exchange);

    // assert
    assertThat(target).isPresent();
    assertThat(target.get().getMessage().getBody(String.class)).isEqualTo(TEST_RESPONSE);
  }
}
