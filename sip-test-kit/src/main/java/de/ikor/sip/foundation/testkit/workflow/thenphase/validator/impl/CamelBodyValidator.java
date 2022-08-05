package de.ikor.sip.foundation.testkit.workflow.thenphase.validator.impl;

import static org.apache.camel.support.MessageHelper.extractBodyAsString;
import static org.apache.camel.support.MessageHelper.resetStreamCache;

import de.ikor.sip.foundation.testkit.util.RegexUtil;
import de.ikor.sip.foundation.testkit.workflow.thenphase.result.ValidationResult;
import de.ikor.sip.foundation.testkit.workflow.thenphase.validator.ExchangeValidator;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/** Validator for body of a request in Camel */
@Component
@AllArgsConstructor
public class CamelBodyValidator implements ExchangeValidator {

  /**
   * Invokes compare body content
   *
   * @param actualResult Result of test execution
   * @param expectedResponse Expected result of test execution
   * @return {@link ValidationResult}
   */
  @Override
  public ValidationResult execute(Exchange actualResult, Exchange expectedResponse) {
    resetStreamCache(actualResult.getMessage());
    String actual = extractBodyAsString(actualResult.getMessage());
    String expected = extractBodyAsString(expectedResponse.getMessage());
    boolean result = false;
    if (isNoBodyExpected(actual, expected)) {
      result = true;
    } else if (isBodyExpected(actual, expected)) {
      result = RegexUtil.compare(expected, actual);
    }
    return new ValidationResult(
        result, result ? "Body validation successful" : "Body validation unsuccessful");
  }

  private boolean isBodyExpected(String actual, String expected) {
    // avoid NPE in regex compare and matching any empty string
    return actual != null && !expected.isEmpty();
  }

  private boolean isNoBodyExpected(String actual, String expected) {
    // match if actual body is empty or null when expected is defined as empty
    return (actual == null && expected.isEmpty())
        || (actual != null && actual.isEmpty() && expected.isEmpty());
  }

  @Override
  public boolean isApplicable(Exchange executionResult, Exchange expectedResponse) {
    return expectedResponse != null && extractBodyAsString(expectedResponse.getMessage()) != null;
  }
}
