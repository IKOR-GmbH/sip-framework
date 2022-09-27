package de.ikor.sip.foundation.testkit.configurationproperties.models;

import static de.ikor.sip.foundation.core.util.SIPExchangeHelper.filterNonSerializableHeaders;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.apache.camel.Exchange;
import org.apache.camel.support.MessageHelper;

/** Class that holds a single message used in test cases */
@Data
public class MessageProperties {
  private String body;
  private Map<String, Object> headers = new HashMap<>();

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
}
