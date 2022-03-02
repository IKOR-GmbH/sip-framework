package de.ikor.sip.testframework.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.testframework.configurationproperties.models.MessageProperties;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SIPExchangeHelperTest {

  private static final String BODY = "body";

  private Exchange exchange;
  private Message message;
  private Map<String, Object> headers;

  @BeforeEach
  void setup() {
    exchange = mock(Exchange.class);
    headers = new HashMap<>();
    message = mock(Message.class);
    when(exchange.getMessage()).thenReturn(message);
    when(message.getBody()).thenReturn(BODY);
    when(message.getHeaders()).thenReturn(headers);
  }

  @Test
  void When_filterNonSerializableHeaders_Expect_FilteredHeaders() {

    headers.put("empty", null);
    headers.put("nonempty", "sth");

    Map<String, Object> result = SIPExchangeHelper.filterNonSerializableHeaders(exchange);

    assertThat(result.get("empty")).isNull();
    assertThat(result.get("nonempty")).isNotNull();
  }

  @Test
  void mapToMessageProperties() {
    Exchange exchange = mock(Exchange.class);
    Map<String, Object> headers = new HashMap<>();
    Message message = mock(Message.class);
    when(exchange.getMessage()).thenReturn(message);
    when(message.getBody()).thenReturn(BODY);
    when(message.getHeaders()).thenReturn(headers);

    MessageProperties messageProperties = SIPExchangeHelper.mapToMessageProperties(exchange);

    assertThat(messageProperties.getBody()).isEqualTo(BODY);
    assertThat(messageProperties.getHeaders()).isEqualTo(headers);
  }
}
