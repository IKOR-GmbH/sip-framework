package de.ikor.sip.testframework;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import de.ikor.sip.testframework.workflow.TestCase;
import de.ikor.sip.testframework.workflow.TestExecutionStatus;
import de.ikor.sip.testframework.workflow.TestRunner;
import de.ikor.sip.testframework.workflow.givenphase.ReportActivityProxyExtension;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** SIP batch test run */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class SIPBatchTest {

  @Autowired private TestRunner testRunner;
  @Autowired private List<TestCase> testCases;
  @Autowired private ReportActivityProxyExtension reportActivityProxyExtension;

  @BeforeAll
  void setup() {
    reportActivityProxyExtension.setTestCases(testCases);
  }

  @MethodSource
  @DisplayName("Batch Tests")
  @ParameterizedTest(name = "{0}")
  void testCaseArguments(String name, TestCase testCase) {
    assumeThat(isValid(testCase)).isTrue().withFailMessage("No validation defined in then-expect");
    assertThat(testRunner.run(testCase)).isTrue();
  }

  // Required for parameterized test - method name must match
  private Stream<Arguments> testCaseArguments() {
    return this.testCases.stream().map(testCase -> Arguments.of(testCase.getTestName(), testCase));
  }

  private boolean isValid(TestCase testCase) {
    TestExecutionStatus testExecutionStatus = testCase.getTestExecutionStatus();
    return !testExecutionStatus.getMockReports().isEmpty()
        || testExecutionStatus.getAdapterReport().getExpectedResponse() != null;
  }
}
