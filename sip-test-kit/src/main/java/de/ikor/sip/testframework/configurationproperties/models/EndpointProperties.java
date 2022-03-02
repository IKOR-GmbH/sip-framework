package de.ikor.sip.testframework.configurationproperties.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Class that defines an element of a test. Each element holds a {@link MessageProperties} */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointProperties {
  private String endpoint;

  @JsonAlias({"with", "returning", "having", "message"})
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
