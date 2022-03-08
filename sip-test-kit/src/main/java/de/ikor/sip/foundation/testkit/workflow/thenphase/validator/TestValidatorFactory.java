package de.ikor.sip.foundation.testkit.workflow.thenphase.validator;

import de.ikor.sip.foundation.testkit.exception.ExceptionType;
import de.ikor.sip.foundation.testkit.exception.TestCaseInitializationException;
import de.ikor.sip.foundation.testkit.workflow.thenphase.result.ValidationType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Factory class that creates new ResultValidator */
@Component
@RequiredArgsConstructor
public class TestValidatorFactory {

  private final List<TestCaseValidator> testCaseValidators;

  /**
   * Get a validator based on given parameters
   *
   * @param validationType {@link ValidationType}
   * @return validator that fits the parameters
   */
  public TestCaseValidator getValidator(ValidationType validationType) {
    for (TestCaseValidator testCaseValidator : testCaseValidators) {
      if (testCaseValidator.isApplicable(validationType)) {
        return testCaseValidator;
      }
    }
    throw new TestCaseInitializationException(
        "Validator of type:" + validationType + " is not yet defined.",
        ExceptionType.RESULT_VALIDATOR);
  }
}
