package de.ikor.sip.foundation.testkit.util;

import de.ikor.sip.foundation.core.util.SIPExchangeHelper;
import de.ikor.sip.foundation.testkit.configurationproperties.models.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.support.MessageHelper;

/** Utility class that changes the {@link Exchange} */
@Slf4j
public class TestKitHelper extends SIPExchangeHelper {

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
