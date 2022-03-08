package de.ikor.sip.foundation.testkit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.TestCase;
import de.ikor.sip.foundation.testkit.workflow.TestExecutionStatus;
import de.ikor.sip.foundation.testkit.workflow.TestRunner;
import de.ikor.sip.foundation.testkit.workflow.reporting.resultprocessor.ResultProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestRunnerTest {

  private TestRunner testRunner;
  @Mock private ResultProcessor resultProcessor;

  @BeforeEach
  private void setUp() {
    testRunner = new TestRunner(resultProcessor);
  }

  @Test
  void When_runBuildTest_WithSuccessfulVerification_Then_Success() {
    // arrange
    TestCase testCase = mock(TestCase.class);
    when(testCase.getTestExecutionStatus())
        .thenReturn(new TestExecutionStatus("test").setSuccessfulExecution(true));

    // act + assert
    assertThat(testRunner.run(testCase)).isTrue();
    verify(testCase).run();
  }

  @Test
  void When_runBuildTest_WithoutSuccessfulVerification_Then_Fail() {
    // arrange
    TestCase testCase = mock(TestCase.class);
    when(testCase.getTestExecutionStatus())
        .thenReturn(new TestExecutionStatus("test").setSuccessfulExecution(false));

    // act + assert
    assertThat(testRunner.run(testCase)).isFalse();
    verify(testCase).run();
  }
}
