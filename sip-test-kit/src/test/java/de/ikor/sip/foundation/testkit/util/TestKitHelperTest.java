package de.ikor.sip.foundation.testkit.util;

import static de.ikor.sip.foundation.core.proxies.ProcessorProxy.TEST_MODE_HEADER;
import static de.ikor.sip.foundation.testkit.util.TestKitHelper.*;
import static de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker.TEST_NAME_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.testkit.configurationproperties.models.EndpointProperties;
import de.ikor.sip.foundation.testkit.configurationproperties.models.MessageProperties;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestKitHelperTest {

  private static final String BODY = "body";
  private static final String ROUTE_ID = "routeId";

  private Exchange exchange;
  private ExtendedCamelContext camelContext;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
    exchange = mock(Exchange.class);
    Map<String, Object> headers = new HashMap<>();
    Message message = mock(Message.class);
    when(exchange.getMessage()).thenReturn(message);
    when(message.getBody()).thenReturn(BODY);
    when(message.getHeaders()).thenReturn(headers);
  }

  @Test
  void GIVEN_simpleBody_WHEN_mapToMessageProperties_THEN_expectValidBody() {
    Exchange exchange = mock(Exchange.class);
    Map<String, Object> headers = new HashMap<>();
    Message message = mock(Message.class);
    when(exchange.getMessage()).thenReturn(message);
    when(message.getBody()).thenReturn(BODY);
    when(message.getHeaders()).thenReturn(headers);

    MessageProperties actual = MessageProperties.mapToMessageProperties(exchange);

    assertThat(actual.getBody()).isEqualTo(BODY);
    assertThat(actual.getHeaders()).isEqualTo(headers);
  }

  @Test
  void GIVEN_route_WHEN_resolveEndpoint_THEN_expectEndpoint() {
    // assert
    Route route = mock(Route.class);
    Endpoint expectedEndpoint = mock(Endpoint.class);
    when(camelContext.getRoute(ROUTE_ID)).thenReturn(route);
    when(route.getEndpoint()).thenReturn(expectedEndpoint);
    when(exchange.getProperty(Mock.CONNECTOR_ID_EXCHANGE_PROPERTY)).thenReturn(ROUTE_ID);

    // act
    Endpoint actualEndpoint = resolveEndpoint(exchange, camelContext);

    // arrange
    assertThat(actualEndpoint).isEqualTo(expectedEndpoint);
  }

  @Test
  void GIVEN_noRoute_WHEN_resolveEndpoint_THEN_expectSIPFrameworkException() {
    // act & arrange
    assertThrows(SIPFrameworkException.class, () -> resolveEndpoint(exchange, camelContext));
  }

  @Test
  void GIVEN_routeId_WHEN_resolveConsumer_THEN_expectConsumer() {
    // assert
    Route route = mock(Route.class);
    Consumer expectedConsumer = mock(Consumer.class);
    when(camelContext.getRoute(ROUTE_ID)).thenReturn(route);
    when(route.getConsumer()).thenReturn(expectedConsumer);
    when(exchange.getProperty(Mock.CONNECTOR_ID_EXCHANGE_PROPERTY)).thenReturn(ROUTE_ID);

    // act
    Consumer actualConsumer = resolveConsumer(exchange, camelContext);

    // arrange
    assertThat(actualConsumer).isEqualTo(expectedConsumer);
  }

  @Test
  void GIVEN_properties_WHEN_parseExchangeProperties_THEN_expectExchangeWithValues() {
    // assert
    EndpointProperties properties = new EndpointProperties();
    properties.setConnectorId("routeId");
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setBody("body");
    messageProperties.setHeaders(Map.of("headerKey", "value"));
    properties.setMessage(messageProperties);

    // act
    Exchange actual = parseExchangeProperties(properties, camelContext);

    // arrange
    assertThat(actual.getMessage().getBody()).isEqualTo("body");
    assertThat(actual.getMessage().getHeaders()).containsEntry("headerKey", "value");
  }

  @Test
  void GIVEN_noProperties_WHEN_parseExchangeProperties_THEN_expectExchangeWithValues() {
    // act
    Exchange actual = parseExchangeProperties(null, camelContext);

    // arrange
    assertThat(actual.getMessage().getBody()).isNull();
    assertThat(actual.getMessage().getHeaders()).isEmpty();
  }

  @ParameterizedTest(name = "Using input headerKey: {0}")
  @ValueSource(strings = {TEST_NAME_HEADER, TEST_MODE_HEADER})
  void GIVEN_sipTestKitHeader_WHEN_isTestKitHeader_THEN_expectTrue(String headerKey) {
    // act + assert
    assertThat(isTestKitHeader(headerKey)).isTrue();
  }

  @Test
  void GIVEN_customHeader_WHEN_isTestKitHeader_THEN_expectFalse() {
    // act + assert
    assertThat(isTestKitHeader("customHeaderKey")).isFalse();
  }
}
