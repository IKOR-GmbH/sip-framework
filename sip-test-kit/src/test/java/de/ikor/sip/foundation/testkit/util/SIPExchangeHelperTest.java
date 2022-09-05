package de.ikor.sip.foundation.testkit.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.testkit.configurationproperties.models.EndpointProperties;
import de.ikor.sip.foundation.testkit.configurationproperties.models.MessageProperties;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SIPExchangeHelperTest {

  private static final String SERIALIZABLE_DEFAULT_VALUE = "This is non serializable value";
  private static final String BODY = "body";
  private static final String ROUTE_ID = "routeId";

  private Exchange exchange;
  private Map<String, Object> headers;
  private ExtendedCamelContext camelContext;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
    exchange = mock(Exchange.class);
    headers = new HashMap<>();
    Message message = mock(Message.class);
    when(exchange.getMessage()).thenReturn(message);
    when(message.getBody()).thenReturn(BODY);
    when(message.getHeaders()).thenReturn(headers);
  }

  @Test
  void GIVEN_differentHeaderValues_WHEN_filterNonSerializableHeaders_THEN_getOnlyFilteredHeaders() {

    headers.put("empty", null);
    headers.put("nonempty", "sth");

    Map<String, Object> result = SIPExchangeHelper.filterNonSerializableHeaders(exchange);

    assertThat(result.get("empty")).isNull();
    assertThat(result.get("nonempty")).isNotNull();
  }

  @Test
  void
      GIVEN_nonSerializableValue_WHEN_reassignNonSerializableValue_THEN_expectSerializableDefaultValue() {
    // arrange
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    Exchange nonserializableValue = exchangeBuilder.build();

    // act
    String actual =
        (String) SIPExchangeHelper.reassignNonSerializableValue("test", nonserializableValue);

    // assert
    assertThat(actual).isEqualTo(SERIALIZABLE_DEFAULT_VALUE);
  }

  @Test
  void GIVEN_simpleBody_WHEN_mapToMessageProperties_THEN_expectValidBody() {
    Exchange exchange = mock(Exchange.class);
    Map<String, Object> headers = new HashMap<>();
    Message message = mock(Message.class);
    when(exchange.getMessage()).thenReturn(message);
    when(message.getBody()).thenReturn(BODY);
    when(message.getHeaders()).thenReturn(headers);

    MessageProperties actual = SIPExchangeHelper.mapToMessageProperties(exchange);

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
    when(exchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY)).thenReturn(ROUTE_ID);

    // act
    Endpoint actualEndpoint = SIPExchangeHelper.resolveEndpoint(exchange, camelContext);

    // arrange
    assertThat(actualEndpoint).isEqualTo(expectedEndpoint);
  }

  @Test
  void GIVEN_noRoute_WHEN_resolveEndpoint_THEN_expectIllegalArgumentException() {
    // act & arrange
    assertThrows(
        IllegalArgumentException.class,
        () -> SIPExchangeHelper.resolveEndpoint(exchange, camelContext));
  }

  @Test
  void GIVEN_routeId_WHEN_resolveConsumer_THEN_expectConsumer() {
    // assert
    Route route = mock(Route.class);
    Consumer expectedConsumer = mock(Consumer.class);
    when(camelContext.getRoute(ROUTE_ID)).thenReturn(route);
    when(route.getConsumer()).thenReturn(expectedConsumer);
    when(exchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY)).thenReturn(ROUTE_ID);

    // act
    Consumer actualConsumer = SIPExchangeHelper.resolveConsumer(exchange, camelContext);

    // arrange
    assertThat(actualConsumer).isEqualTo(expectedConsumer);
  }

  @Test
  void GIVEN_properties_WHEN_parseExchangeProperties_THEN_expectExchangeWithValues() {
    // assert
    EndpointProperties properties = new EndpointProperties();
    properties.setEndpoint("routeId");
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setBody("body");
    messageProperties.setHeaders(Map.of("headerKey", "value"));
    properties.setMessage(messageProperties);

    // act
    Exchange actual = SIPExchangeHelper.parseExchangeProperties(properties, camelContext);

    // arrange
    assertThat(actual.getMessage().getBody()).isEqualTo("body");
    assertThat(actual.getMessage().getHeaders()).containsEntry("headerKey", "value");
  }

  @Test
  void GIVEN_noProperties_WHEN_parseExchangeProperties_THEN_expectExchangeWithValues() {
    // act
    Exchange actual = SIPExchangeHelper.parseExchangeProperties(null, camelContext);

    // arrange
    assertThat(actual.getMessage().getBody()).isNull();
    assertThat(actual.getMessage().getHeaders()).isEmpty();
  }
}
