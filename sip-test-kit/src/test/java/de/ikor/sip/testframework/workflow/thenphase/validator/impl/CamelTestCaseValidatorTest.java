package de.ikor.sip.testframework.workflow.thenphase.validator.impl;

import de.ikor.sip.testframework.workflow.TestExecutionStatus;
import de.ikor.sip.testframework.workflow.reporting.model.MockReport;
import de.ikor.sip.testframework.workflow.reporting.model.SIPAdapterExecutionReport;
import de.ikor.sip.testframework.workflow.thenphase.result.ValidationResult;
import de.ikor.sip.testframework.workflow.thenphase.result.ValidationType;
import de.ikor.sip.testframework.workflow.thenphase.validator.ExchangeValidator;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CamelTestCaseValidatorTest {

    private CamelTestCaseValidator subject;
    private ExchangeValidator exchangeValidator;

    @BeforeEach
    void setUp() {
        List<ExchangeValidator> validators = new ArrayList<>();
        exchangeValidator = mock(ExchangeValidator.class);
        validators.add(exchangeValidator);
        subject = new CamelTestCaseValidator(validators);
    }

    @Test
    void getValidationType() {
        // act + assert
        assertThat(subject.getValidationType()).isEqualTo(ValidationType.FULL);
    }

    @Test
    void When_validate_Expect_TestExecutionStatusUpdated() {
        // arrange
        Exchange actual = mock(Exchange.class);
        Exchange expected = mock(Exchange.class);
        Message actualMessage = mock(Message.class);
        Message expectedMessage = mock(Message.class);
        when(actual.getMessage()).thenReturn(actualMessage);
        when(expected.getMessage()).thenReturn(expectedMessage);
        when(actualMessage.getBody()).thenReturn("body");
        when(actualMessage.getHeaders()).thenReturn(new HashMap<>());
        when(expectedMessage.getHeaders()).thenReturn(new HashMap<>());

        TestExecutionStatus testExecutionStatus = new TestExecutionStatus();
        SIPAdapterExecutionReport adapterReport = new SIPAdapterExecutionReport();
        Map<String, MockReport> mockReports = new HashMap<>();
        adapterReport.setActualResponse(actual);
        adapterReport.setExpectedResponse(expected);
        testExecutionStatus.setAdapterReport(adapterReport);
        testExecutionStatus.setMockReports(mockReports);

        when(exchangeValidator.isApplicable(actual, expected)).thenReturn(true);
        when(exchangeValidator.execute(actual, expected)).thenReturn(new ValidationResult(true, "success"));

        assertThat(testExecutionStatus.isSuccessfulExecution()).isFalse();

        subject.validate(testExecutionStatus);

        assertThat(testExecutionStatus.isSuccessfulExecution()).isTrue();
    }
}