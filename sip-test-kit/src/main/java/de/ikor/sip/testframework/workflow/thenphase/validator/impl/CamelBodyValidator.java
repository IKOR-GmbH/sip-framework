package de.ikor.sip.testframework.workflow.thenphase.validator.impl;

import de.ikor.sip.testframework.util.RegexUtil;
import de.ikor.sip.testframework.workflow.thenphase.result.ValidationResult;
import de.ikor.sip.testframework.workflow.thenphase.validator.ExchangeValidator;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.support.MessageHelper;
import org.springframework.stereotype.Component;

/** Validator for body of a request in Camel */
@Component
@AllArgsConstructor
public class CamelBodyValidator implements ExchangeValidator {

  /**
   * Invokes compare body content
   *
   * @param executionResult Result of test execution
   * @param expectedResponse Expected result of test execution
   * @return {@link ValidationResult}
   */
  @Override
  public ValidationResult execute(Exchange executionResult, Exchange expectedResponse) {
    boolean result =
        RegexUtil.compare(
            MessageHelper.extractBodyAsString(expectedResponse.getMessage()),
            MessageHelper.extractBodyAsString(executionResult.getMessage()));
    return new ValidationResult(
        result, result ? "Body validation successful" : "Body validation unsuccessful");
  }

  @Override
  public boolean isApplicable(Exchange executionResult, Exchange expectedResponse) {
    return expectedResponse != null
        && MessageHelper.extractBodyAsString(expectedResponse.getMessage()) != null;
  }
}
