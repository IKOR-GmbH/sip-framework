package de.ikor.sip.foundation.testkit.workflow.reporting.model;

import de.ikor.sip.foundation.testkit.configurationproperties.models.MessageProperties;
import de.ikor.sip.foundation.testkit.util.SIPExchangeHelper;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.camel.Exchange;

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
    this.setExpectedMessage(SIPExchangeHelper.mapToMessageProperties(expected));
    return this;
  }
}
