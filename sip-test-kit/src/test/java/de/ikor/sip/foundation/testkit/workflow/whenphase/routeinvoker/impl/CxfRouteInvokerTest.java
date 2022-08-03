package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

class CxfRouteInvokerTest {

  private static final String RESPONSE_BODY =
      "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:AddBookResponse xmlns:ns2=\"http://www.cleverbuilder.com/BookService/\"><ns2:Book><ID>1</ID><Title>Camel in Action</Title><Author>Claus Ibsen</Author></ns2:Book></ns2:AddBookResponse></soap:Body></soap:Envelope>";

  private CxfRouteInvoker subject;
  private RestTemplate restTemplate;
  private ExtendedCamelContext camelContext;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
    RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
    restTemplate = mock(RestTemplate.class);
    Environment environment = mock(Environment.class);
    subject = new CxfRouteInvoker(camelContext, environment, restTemplateBuilder);
    Endpoint endpoint = mock(Endpoint.class);
    subject.setEndpoint(endpoint);

    when(environment.getProperty("local.server.port")).thenReturn("8081");
    when(restTemplateBuilder.build()).thenReturn(restTemplate);
  }

  @ParameterizedTest(name = "Using input body: {0}")
  @ValueSource(strings = {RESPONSE_BODY})
  @NullSource
  void GIVEN_emptyRequest_WHEN_executeTask_THEN_validateGoodResponse(String inputBody) {
    // arrange
    Exchange exchange = createExchange(new HashMap<>());
    CxfRouteInvoker spySubject = spy(subject);
    doReturn("test").when(spySubject).getCxfEndpointAddress(any());
    ResponseEntity<String> routeExpectedResponse = new ResponseEntity<>(inputBody, HttpStatus.OK);
    when(restTemplate.exchange(
            anyString(),
            any(HttpMethod.class),
            any(),
            ArgumentMatchers.<ParameterizedTypeReference>any()))
        .thenReturn(routeExpectedResponse);

    // act
    Exchange actualExchange = spySubject.invoke(exchange);

    // assert
    assertThat(actualExchange.getMessage().getBody()).isEqualTo(inputBody);
  }

  private Exchange createExchange(Map<String, Object> headers) {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext).withBody("");
    headers.forEach(exchangeBuilder::withHeader);
    return exchangeBuilder.build();
  }
}