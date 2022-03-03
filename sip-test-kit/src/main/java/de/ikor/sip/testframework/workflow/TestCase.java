package de.ikor.sip.testframework.workflow;

import de.ikor.sip.testframework.workflow.givenphase.Mock;
import de.ikor.sip.testframework.workflow.thenphase.validator.TestCaseValidator;
import de.ikor.sip.testframework.workflow.whenphase.ExecutionWrapper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.camel.Exchange;

/** Class containing everything necessary for test execution. */
@Data
@AllArgsConstructor
public class TestCase {
  private String testName;
  private List<Mock> mocks;
  private ExecutionWrapper executionWrapper;
  private TestCaseValidator validator;
  private TestExecutionStatus testExecutionStatus;

  /** Start Test execution */
  public void run() {
    executeGivenPhase();
    Exchange exchange = executeWhenPhase();
    testExecutionStatus.getAdapterReport().setActualResponse(exchange);
    executeThenPhase(testExecutionStatus);
  }

  /** Clear mocks after execution */
  public void clearMocks() {
    mocks.forEach(Mock::clear);
  }

  /**
   * Signal this test case that the Exception happened so the TestReport can be updated
   *
   * @param exception that happened
   */
  void reportExecutionException(Exception exception) {
    testExecutionStatus.setSuccessfulExecution(false).setWorkflowException(exception);
  }

  /** Prepare and active mocks for test */
  private void executeGivenPhase() {
    for (Mock mock : mocks) {
      mock.setBehavior(testExecutionStatus);
    }
  }

  private Exchange executeWhenPhase() {
    return executionWrapper.execute();
  }

  private void executeThenPhase(TestExecutionStatus testExecutionStatus) {
    validator.validate(testExecutionStatus);
  }
}
