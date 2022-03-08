package de.ikor.sip.testframework.workflow;

import de.ikor.sip.testframework.workflow.givenphase.Mock;
import de.ikor.sip.testframework.workflow.thenphase.validator.TestCaseValidator;
import de.ikor.sip.testframework.workflow.whenphase.ExecutionWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class TestCaseTest {

    private static final String TEST_NAME = "name";
    public static final String EXCEPTION_MESSAGE = "exception message";
    TestCase subject;
    List<Mock> mocks;
    ExecutionWrapper executionWrapper;
    TestCaseValidator validator;
    TestExecutionStatus testExecutionStatus;
    Mock mock;

    @BeforeEach
    void setUp() {
        mock = mock(Mock.class);
        mocks = new ArrayList<>();
        mocks.add(mock);
        executionWrapper = mock(ExecutionWrapper.class);
        validator = mock(TestCaseValidator.class);
        testExecutionStatus = new TestExecutionStatus(TEST_NAME);
        subject = new TestCase(TEST_NAME, mocks, executionWrapper, validator, testExecutionStatus);
    }

    @Test
    void When_run_Expect_AllPhasesExecuted() {
        // arrange
        Exchange exchange = mock(Exchange.class);
        Message message = mock(Message.class);
        when(exchange.getMessage()).thenReturn(message);
        when(executionWrapper.execute()).thenReturn(exchange);
        doNothing().when(validator).validate(testExecutionStatus);

        // act
        subject.run();

        // assert
        verify(mock, times(1)).setBehavior(testExecutionStatus);
        verify(validator, times(1)).validate(testExecutionStatus);
        assertThat(testExecutionStatus.getAdapterReport().getActualResponse()).isEqualTo(exchange);
    }

    @Test
    void When_clearMocks_Expect_MockClearCalled() {
        // act
        subject.clearMocks();

        // assert
        verify(mock, times(1)).clear();
    }

    @Test
    void reportExecutionException() {
        // arrange
        RuntimeException ex = new RuntimeException(EXCEPTION_MESSAGE);

        // act
        subject.reportExecutionException(ex);

        // assert
        assertThat(testExecutionStatus.isSuccessfulExecution()).isFalse();
        assertThat(testExecutionStatus.getWorkflowException()).isEqualTo(ex);
        assertThat(testExecutionStatus.getWorkflowExceptionMessage()).contains(EXCEPTION_MESSAGE);
    }
}