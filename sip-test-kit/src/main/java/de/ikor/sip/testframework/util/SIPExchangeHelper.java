package de.ikor.sip.testframework.util;

import de.ikor.sip.testframework.configurationproperties.models.MessageProperties;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchangeHolder;
import org.apache.camel.support.MessageHelper;

/** Utility class that changes the {@link Exchange} */
public class SIPExchangeHelper extends DefaultExchangeHolder {

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
                filteredHeaders.put(k, value);
              }
            });
    return filteredHeaders;
  }

  /**
   * Creates a {@link MessageProperties} from the {@link Exchange}.
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
}
