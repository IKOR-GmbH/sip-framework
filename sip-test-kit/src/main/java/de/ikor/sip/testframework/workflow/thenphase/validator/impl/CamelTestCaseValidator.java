package de.ikor.sip.testframework.workflow.thenphase.validator.impl;

import de.ikor.sip.testframework.workflow.TestExecutionStatus;
import de.ikor.sip.testframework.workflow.reporting.model.EndpointValidationOutcome;
import de.ikor.sip.testframework.workflow.reporting.model.MockReport;
import de.ikor.sip.testframework.workflow.reporting.model.SIPAdapterExecutionReport;
import de.ikor.sip.testframework.workflow.thenphase.result.ValidationResult;
import de.ikor.sip.testframework.workflow.thenphase.validator.ExchangeValidator;
import de.ikor.sip.testframework.workflow.thenphase.validator.TestCaseValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/** Batch test validator for Camel */
@Slf4j
@Component
@AllArgsConstructor
public class CamelTestCaseValidator implements TestCaseValidator {
  private final List<ExchangeValidator> exchangeValidators;

  /** Validates actual of test execution and forwards it to report service */
  @Override
  public void validate(TestExecutionStatus testExecutionStatus) {
    SIPAdapterExecutionReport adapterReport = testExecutionStatus.getAdapterReport();
    Map<String, MockReport> mockReports = testExecutionStatus.getMockReports();

    this.validateAdapterResponse(adapterReport);
    this.validateMockReports(mockReports);

    boolean isAdapterResultExpected =
        evaluateValidationResults(adapterReport.getValidationResults());
    boolean areAllMocksExpected = mockReports.values().stream().noneMatch(this::isNotSuccess);

    testExecutionStatus.setSuccessfulExecution(isAdapterResultExpected && areAllMocksExpected);
  }

  private void validateAdapterResponse(SIPAdapterExecutionReport adapterExecutionReport) {
    Exchange actual = adapterExecutionReport.getActualResponse();
    Exchange expected = adapterExecutionReport.getExpectedResponse();
    List<ValidationResult> adapterValidationResults = runValidators(actual, expected);
    adapterExecutionReport
        .setValidationResults(adapterValidationResults)
        .setValidatedHeaders(extractValidatedHeaders(actual, expected))
        .setAdapterExceptionMessage(actual.getException());
  }

  private void validateMockReports(Map<String, MockReport> mockReportMap) {
    mockReportMap.values().stream()
        .filter(mockReport -> mockReport.getExpected() != null)
        .forEach(this::fillMockReport);
  }

  private void fillMockReport(MockReport mockReport) {
    List<ValidationResult> endpointValidationResultList =
        runValidators(mockReport.getActual(), mockReport.getExpected());
    mockReport.setValidated(
        evaluateValidationResults(endpointValidationResultList)
            ? EndpointValidationOutcome.SUCCESSFUL
            : EndpointValidationOutcome.UNSUCCESSFUL);
    mockReport.setValidatedHeaders(
        extractValidatedHeaders(mockReport.getActual(), mockReport.getExpected()));
  }

  private boolean isNotSuccess(MockReport mockReport) {
    return mockReport.getValidated().equals(EndpointValidationOutcome.UNSUCCESSFUL);
  }

  private HashMap<String, Object> extractValidatedHeaders(
      Exchange executionResult, Exchange expectedResponse) {
    HashMap<String, Object> validatedHeaders = new HashMap<>();
    if (executionResult == null || expectedResponse == null) {
      return validatedHeaders;
    }
    expectedResponse
        .getMessage()
        .getHeaders()
        .keySet()
        .forEach(
            key -> {
              if (executionResult.getMessage().getHeader(key) != null) {
                validatedHeaders.put(
                    key, executionResult.getMessage().getHeader(key, String.class));
              }
            });
    return validatedHeaders;
  }

  private boolean evaluateValidationResults(List<ValidationResult> validationResultList) {
    return validationResultList.stream().allMatch(ValidationResult::isSuccess);
  }

  private List<ValidationResult> runValidators(
      Exchange executionResult, Exchange expectedResponse) {
    List<ValidationResult> validationResults = new ArrayList<>();
    for (ExchangeValidator validator : this.exchangeValidators) {
      if (validator.isApplicable(executionResult, expectedResponse)) {
        validationResults.add(validator.execute(executionResult, expectedResponse));
      }
    }

    return validationResults;
  }

  @Override
  public boolean isApplicable() {
    return true;
  }
}
