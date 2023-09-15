package de.ikor.sip.foundation.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SIPExchangeHelperTest {

  private static final String SERIALIZABLE_DEFAULT_VALUE = "This is non serializable value";
  private static final String BODY = "body";

  private Exchange exchange;
  private Message message;
  private Map<String, Object> headers;
  private CamelContext camelContext;

  @BeforeEach
  void setup() {
    camelContext = mock(CamelContext.class);
    exchange = mock(Exchange.class);
    headers = new HashMap<>();
    message = mock(Message.class);
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
}
