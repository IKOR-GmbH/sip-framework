package de.ikor.sip.testframework.workflow.thenphase.validator.impl;

import de.ikor.sip.testframework.util.RegexUtil;
import de.ikor.sip.testframework.workflow.thenphase.result.ValidationResult;
import java.util.concurrent.atomic.AtomicBoolean;

import de.ikor.sip.testframework.workflow.thenphase.validator.ExchangeValidator;
import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/** Validator for headers of a request in Camel */
@Component
@AllArgsConstructor
public class CamelHeaderValidator implements ExchangeValidator {

  /**
   * Invokes compare header content
   *
   * @param executionResult Result of test execution
   * @param expectedResponse Expected result of test execution
   * @return {@link ValidationResult}
   */
  @Override
  public ValidationResult execute(Exchange executionResult, Exchange expectedResponse) {
    AtomicBoolean result = new AtomicBoolean(true);
    expectedResponse
        .getMessage()
        .getHeaders()
        .forEach(
            (key, value) -> {
              if (executionResult.getMessage().getHeader(key) == null
                  || !RegexUtil.compare((String) value, executionResult.getMessage().getHeader(key, String.class))) {
                result.set(false);
              }
            });

    return new ValidationResult(
        result.get(),
        result.get() ? "Header validation successful" : "Header validation unsuccessful");
  }

  @Override
  public boolean isApplicable(Exchange executionResult, Exchange expectedResponse) {
    return expectedResponse != null && !expectedResponse.getMessage().getHeaders().isEmpty();
  }
}
