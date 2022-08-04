package de.ikor.sip.foundation.testkit.workflow.thenphase.validator.impl;

import de.ikor.sip.foundation.testkit.util.RegexUtil;
import de.ikor.sip.foundation.testkit.workflow.thenphase.result.ValidationResult;
import de.ikor.sip.foundation.testkit.workflow.thenphase.validator.ExchangeValidator;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import static org.apache.camel.support.MessageHelper.extractBodyAsString;
import static org.apache.camel.support.MessageHelper.resetStreamCache;

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
    if (actual == null && "".equals(expected)) {
      result = true;
    } else if (actual != null && !"".equals(expected)) { // avoid NPE in regex compare and any match empty string
      result = RegexUtil.compare(expected, actual);
    }
    return new ValidationResult(
        result, result ? "Body validation successful" : "Body validation unsuccessful");
  }

  @Override
  public boolean isApplicable(Exchange executionResult, Exchange expectedResponse) {
    return expectedResponse != null && extractBodyAsString(expectedResponse.getMessage()) != null;
  }
}
