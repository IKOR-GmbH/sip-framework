package de.ikor.sip.testframework.workflow.reporting.model;

import de.ikor.sip.testframework.configurationproperties.models.MessageProperties;
import de.ikor.sip.testframework.util.SIPExchangeHelper;
import de.ikor.sip.testframework.workflow.thenphase.result.ValidationResult;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.camel.Exchange;

import java.util.List;
import java.util.Map;

@Data
public class SIPAdapterExecutionReport {
  private Exchange actualResponse;
  private Exchange expectedResponse;
  private Map<String, Object> validatedHeaders;
  private List<ValidationResult> validationResults;
  private String adapterExceptionMessage;

  @Setter(AccessLevel.PRIVATE)
  private MessageProperties responseMessage;

  public void setActualResponse(Exchange actualResponse) {
    this.actualResponse = actualResponse;
    this.setResponseMessage(SIPExchangeHelper.mapToMessageProperties(actualResponse));
  }

  public SIPAdapterExecutionReport setAdapterExceptionMessage(Exception exception) {
    adapterExceptionMessage = exception != null ? exception.toString() : null;
    return this;
  }
}
