package de.ikor.sip.foundation.testkit.configurationproperties.models;

import static de.ikor.sip.foundation.core.util.SIPExchangeHelper.filterNonSerializableHeaders;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.support.MessageHelper;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

/** Class that holds a single message used in test cases */
@Data
public class MessageProperties {
  private static final String FILE_PATH_PREFIX = "file-name:";
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

  @SneakyThrows
  public String getBody() {
    if (isNotBlank(body) && body.startsWith(FILE_PATH_PREFIX)) {
      String bodyLocation = body.substring(FILE_PATH_PREFIX.length());
      body =
          FileUtils.readFileToString(
              new ClassPathResource(bodyLocation).getFile(), StandardCharsets.UTF_8);
    }
    return body;
  }
}
