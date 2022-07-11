package de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

class CxfRouteProducerTest {

  private static final String RESPONSE_BODY =
      "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><ns2:AddBookResponse xmlns:ns2=\"http://www.cleverbuilder.com/BookService/\"><ns2:Book><ID>1</ID><Title>Camel in Action</Title><Author>Claus Ibsen</Author></ns2:Book></ns2:AddBookResponse></soap:Body></soap:Envelope>";

  private CxfRouteProducer subject;
  private RestTemplate restTemplate;
  private ExtendedCamelContext camelContext;
  private Endpoint endpoint;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
    restTemplate = mock(RestTemplate.class);
    Environment environment = mock(Environment.class);
    subject = new CxfRouteProducer(camelContext, environment, restTemplate);
    endpoint = mock(Endpoint.class);

    when(environment.getProperty("local.server.port")).thenReturn("8081");
  }

  @ParameterizedTest
  @ValueSource(strings = {RESPONSE_BODY})
  @NullSource
  void GIVEN_emptyRequest_WHEN_executeTask_THEN_validateGoodResponse(String inputBody) {
    // arrange
    Exchange exchange = createExchange("", new HashMap<>());
    CxfRouteProducer spySubject = spy(subject);
    doReturn("test").when(spySubject).getCxfEndpointAddress(any());
    ResponseEntity<String> routeExpectedResponse = new ResponseEntity<>(inputBody, HttpStatus.OK);
    when(restTemplate.exchange(
            anyString(),
            any(HttpMethod.class),
            any(),
            ArgumentMatchers.<ParameterizedTypeReference>any()))
        .thenReturn(routeExpectedResponse);

    // act
    Exchange actualExchange = spySubject.executeTask(exchange, endpoint);

    // assert
    assertThat(actualExchange.getMessage().getBody()).isEqualTo(inputBody);
  }

  private Exchange createExchange(String body, Map<String, Object> headers) {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext).withBody(body);
    headers.forEach(exchangeBuilder::withHeader);
    return exchangeBuilder.build();
  }
}
