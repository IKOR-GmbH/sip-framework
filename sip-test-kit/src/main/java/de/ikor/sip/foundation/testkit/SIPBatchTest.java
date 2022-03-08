package de.ikor.sip.foundation.testkit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import de.ikor.sip.foundation.testkit.util.SIPBatchTestArgumentSource;
import de.ikor.sip.foundation.testkit.workflow.TestCase;
import de.ikor.sip.foundation.testkit.workflow.TestExecutionStatus;
import de.ikor.sip.foundation.testkit.workflow.TestRunner;
import de.ikor.sip.foundation.testkit.workflow.givenphase.ReportActivityProxyExtension;
import java.util.List;

import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** SIP batch test run */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class SIPBatchTest {

  @Autowired private TestRunner testRunner;
  @Getter @Autowired private List<TestCase> testCases;
  @Autowired private ReportActivityProxyExtension reportActivityProxyExtension;

  @BeforeAll
  void setup() {
    reportActivityProxyExtension.setTestCases(testCases);
  }

  @ArgumentsSource(SIPBatchTestArgumentSource.class)
  @DisplayName("Batch Tests")
  @ParameterizedTest(name = "{index}: {0}")
  void testCaseArguments(TestCase testCase) {
    assumeThat(isValid(testCase)).withFailMessage("No validation defined in then-expect").isTrue();
    assertThat(testRunner.run(testCase)).isTrue();
  }

  private boolean isValid(TestCase testCase) {
    TestExecutionStatus testExecutionStatus = testCase.getTestExecutionStatus();
    return !testExecutionStatus.getMockReports().isEmpty()
        || testExecutionStatus.getAdapterReport().getExpectedResponse() != null;
  }
}
