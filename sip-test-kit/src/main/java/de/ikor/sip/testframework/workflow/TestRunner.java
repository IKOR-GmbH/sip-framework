package de.ikor.sip.testframework.workflow;

import de.ikor.sip.testframework.workflow.reporting.resultprocessor.ResultProcessor;
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
    try {
      testCase.run();
    } catch (Exception e) {
      log.error("An error occurred while performing test {}.", testCase.getTestName());
      testCase.reportExecutionException(e);
    } finally {
      testCase.clearMocks();
    }
    return testCase.getTestExecutionStatus();
  }
}
