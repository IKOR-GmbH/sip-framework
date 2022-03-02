package de.ikor.sip.testframework.workflow.thenphase.validator;

import de.ikor.sip.testframework.workflow.TestExecutionStatus;
import de.ikor.sip.testframework.workflow.thenphase.result.ValidationType;

/** Parent class for test result validators */
public interface TestCaseValidator {

  /**
   * Validates configured conditions based on data collected during test execution, like adapter
   * response or requests received by endpoint mocks.
   * @param testExecutionStatus - provides test execution data and stores validation results
   */
  void validate(TestExecutionStatus testExecutionStatus);

  /**
   * SIP internal interface, used to separate runtime from batch tests.
   * @return Returns validation type this validator performs
   */
  ValidationType getValidationType();

  /**
   * Is a validator applicable for a test case based on parameters
   *
   * @param validationType {@link ValidationType}
   * @return true if the validator is applicable, false otherwise
   */
  default boolean isApplicable(ValidationType validationType) {
    return validationType.equals(getValidationType());
  }
}
