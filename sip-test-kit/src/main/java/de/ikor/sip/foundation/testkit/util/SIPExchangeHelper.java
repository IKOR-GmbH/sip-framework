package de.ikor.sip.foundation.testkit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ikor.sip.foundation.testkit.configurationproperties.models.MessageProperties;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.support.DefaultExchangeHolder;
import org.apache.camel.support.MessageHelper;

/** Utility class that changes the {@link Exchange} */
@Slf4j
public class SIPExchangeHelper extends DefaultExchangeHolder {

  private static final String SERIALIZABLE_DEFAULT_VALUE = "This is non serializable value";

  /**
   * Filters out all non-serializable headers so they can be used in serializable environment
   *
   * @param exchange whose headers should be checked
   * @return Map of headers that are serializable
   */
  protected static Map<String, Object> filterNonSerializableHeaders(Exchange exchange) {
    Map<String, Object> filteredHeaders = new HashMap<>();
    exchange
        .getMessage()
        .getHeaders()
        .forEach(
            (k, v) -> {
              Object value = getValidHeaderValue(k, v, true);
              if (value != null) {
                filteredHeaders.put(k, reassignNonSerializableValue(k, value));
              }
            });
    return filteredHeaders;
  }

  protected static Object reassignNonSerializableValue(String headerName, Object value) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.writeValue(new ByteArrayOutputStream(), value);
    } catch (IOException e) {
      log.warn("sip.testkit.util.nonserializablevalue_{}", headerName);
      return SERIALIZABLE_DEFAULT_VALUE;
    }
    return value;
  }

  /**
   * Creates a {@link MessageProperties} from the {@link Exchange}
   *
   * @param exchange that should be mapped
   * @return serializable message properties
   */
  public static MessageProperties mapToMessageProperties(Exchange exchange) {
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setBody(MessageHelper.extractBodyAsString(exchange.getMessage()));
    messageProperties.setHeaders(filterNonSerializableHeaders(exchange));
    return messageProperties;
  }

  /**
   * Creates an empty {@link Exchange}
   *
   * @param camelContext is necessary for creation
   * @return exchange
   */
  public static Exchange createEmptyExchange(CamelContext camelContext) {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }
}
