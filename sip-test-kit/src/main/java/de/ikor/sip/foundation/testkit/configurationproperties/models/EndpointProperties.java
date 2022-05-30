package de.ikor.sip.foundation.testkit.configurationproperties.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Class that defines an element of a test. Each element holds a {@link MessageProperties} */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointProperties {
  private String endpoint;

  private MessageProperties message = new MessageProperties();

  public void setWith(MessageProperties message) {
    this.setMessage(message);
  }

  public void setReturning(MessageProperties message) {
    this.setMessage(message);
  }

  public void setHaving(MessageProperties message) {
    this.setMessage(message);
  }
}
