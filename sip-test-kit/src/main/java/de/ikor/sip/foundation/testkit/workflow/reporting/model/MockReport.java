package de.ikor.sip.foundation.testkit.workflow.reporting.model;

import static de.ikor.sip.foundation.testkit.util.SIPExchangeHelper.mapToMessageProperties;

import de.ikor.sip.foundation.testkit.configurationproperties.models.MessageProperties;
import de.ikor.sip.foundation.testkit.workflow.thenphase.result.ValidationResult;
import java.util.List;
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
  private List<ValidationResult> validationResults;

  public MockReport setExpected(Exchange expected) {
    this.expected = expected;
    this.setExpectedMessage(mapToMessageProperties(expected));
    return this;
  }
}
