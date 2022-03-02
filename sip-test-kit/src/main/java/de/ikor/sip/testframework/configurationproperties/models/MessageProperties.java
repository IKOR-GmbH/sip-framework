package de.ikor.sip.testframework.configurationproperties.models;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/** Class that holds a single message used in test cases */
@Data
public class MessageProperties {
  private String body;
  private Map<String, Object> headers = new HashMap<>();
}
