package de.ikor.sip.testframework.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestExecutionStatusTest {

  private static final String EXCEPTION_MESSAGE = "exception message";
  private static final String MOCK_REPORT_KEY = "any";
  TestExecutionStatus subject;

  @BeforeEach
  void setUp() {
    subject = new TestExecutionStatus("test name");
  }

  @Test
  void When_setExpectedAdapterResponse_Expect_AdapterReportUpdated() {
    // arrange
    Exchange exchange = mock(Exchange.class);

    // act
    subject.setExpectedAdapterResponse(exchange);

    // assert
    assertThat(subject.getAdapterReport().getExpectedResponse()).isEqualTo(exchange);
  }

  @Test
  void When_setWorkflowException_Expect_WorkflowExceptionAndMessageSet() {
    // arrange
    Exception ex = new RuntimeException(EXCEPTION_MESSAGE);

    // act
    subject.setWorkflowException(ex);

    // assert
    assertThat(subject.getWorkflowException()).isEqualTo(ex);
    assertThat(subject.getWorkflowExceptionMessage())
        .startsWith("Error occurred during workflow of the test: " + EXCEPTION_MESSAGE);
  }

  @Test
  void When_getMockReport_With_NoMockReport_Then_AddNewMockReport() {
    // act + assert
    assertThat(subject.getMockReports().get(MOCK_REPORT_KEY)).isNull();
    assertThat(subject.getMockReport(MOCK_REPORT_KEY)).isNotNull();
    assertThat(subject.getMockReports().get(MOCK_REPORT_KEY)).isNotNull();
  }
}
