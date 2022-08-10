package de.ikor.sip.foundation.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchangeHolder;

/** Utility class that changes the {@link Exchange} */
@Slf4j
public class SIPExchangeHelper extends DefaultExchangeHolder {

  private static final String SERIALIZABLE_DEFAULT_VALUE = "This is non serializable value";

  /**
   * Filters out all non-serializable headers, so they can be used in serializable environment
   *
   * @param exchange whose headers should be checked
   * @return Map of headers that are serializable
   */
  public static Map<String, Object> filterNonSerializableHeaders(Exchange exchange) {
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
}
