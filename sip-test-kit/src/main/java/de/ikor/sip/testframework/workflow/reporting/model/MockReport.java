package de.ikor.sip.testframework.workflow.reporting.model;

import de.ikor.sip.testframework.configurationproperties.models.MessageProperties;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.camel.Exchange;

import java.util.Map;

import static de.ikor.sip.testframework.util.SIPExchangeHelper.mapToMessageProperties;

/** Report for a single Mock */
@Data
@Accessors(chain = true)
public class MockReport {
  private EndpointValidationOutcome validated = EndpointValidationOutcome.SKIPPED;
  private Exchange expected;
  private MessageProperties expectedMessage;
  private Exchange actual;
  private MessageProperties actualMessage;
  private Map<String, Object> validatedHeaders;

  public MockReport setExpected(Exchange expected) {
    this.expected = expected;
    this.setExpectedMessage(mapToMessageProperties(expected));
    return this;
  }
}
