package de.ikor.sip.foundation.testkit.workflow;

import de.ikor.sip.foundation.testkit.workflow.reporting.resultprocessor.ResultProcessor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Main class for running tests. */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestRunner {
  private final ResultProcessor resultProcessor;

  /**
   * Run a single build test case.
   *
   * @param testCase {@link TestCase}
   */
  public boolean run(TestCase testCase) {
    TestExecutionStatus testExecutionStatus = executeTest(testCase);
    resultProcessor.process(testExecutionStatus);
    return testExecutionStatus.isSuccessfulExecution();
  }

  public TestExecutionStatus executeTest(TestCase testCase) {
    Optional<Exception> exception = testCase.getTestExecutionStatus().getWorkflowException();
    try {
      if (exception.isEmpty()) {
        testCase.run();
      } else {
        handleTestException(testCase, exception.get());
      }
    } catch (Exception e) {
      handleTestException(testCase, e);
    } finally {
      testCase.clearMocks();
    }
    return testCase.getTestExecutionStatus();
  }

  private void handleTestException(TestCase testCase, Exception e) {
    log.error("sip.testkit.workflow.testrunerror_{}", testCase.getTestName());
    testCase.reportExecutionException(e);
  }
}
