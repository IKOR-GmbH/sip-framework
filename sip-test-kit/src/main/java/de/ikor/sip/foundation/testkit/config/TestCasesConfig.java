package de.ikor.sip.foundation.testkit.config;

import static de.ikor.sip.foundation.testkit.util.SIPExchangeHelper.parseExchangeProperties;
import static java.util.stream.Collectors.toList;

import de.ikor.sip.foundation.testkit.configurationproperties.TestCaseBatchDefinition;
import de.ikor.sip.foundation.testkit.configurationproperties.TestCaseDefinition;
import de.ikor.sip.foundation.testkit.exception.NoRouteInvokerException;
import de.ikor.sip.foundation.testkit.exception.handler.ExceptionLogger;
import de.ikor.sip.foundation.testkit.workflow.TestCase;
import de.ikor.sip.foundation.testkit.workflow.TestCaseCollector;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import de.ikor.sip.foundation.testkit.workflow.givenphase.MockFactory;
import de.ikor.sip.foundation.testkit.workflow.thenphase.validator.TestCaseValidator;
import de.ikor.sip.foundation.testkit.workflow.whenphase.ExecutionWrapper;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvokerFactory;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Configuration class used for creation of batch test cases based on test definitions. */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestCasesConfig {

  private final RouteInvokerFactory routeInvokerFactory;
  private final MockFactory mockFactory;
  private final CamelContext camelContext;
  private final TestCaseValidator testCaseValidator;
  private final TestExecutionStatusFactory executionStatusFactory;
  private final TestCaseBatchDefinition testCaseBatchDefinition;
  private final TestCaseCollector testCaseCollector;

  /** Creates test cases based on batch test cases definition. */
  @EventListener(ApplicationReadyEvent.class)
  public void generateTestCases() {
    List<TestCase> testCases = new LinkedList<>();
    for (TestCaseDefinition testCaseDefinition : testCaseBatchDefinition.getTestCaseDefinitions()) {
      try {
        testCases.add(generateTestCase(testCaseDefinition));
      } catch (Exception e) {
        ExceptionLogger.logTestCaseException(e, testCaseDefinition.getTitle());
      }
    }
    validateTestCaseInitializations(testCases);
    testCaseCollector.setTestCases(testCases);
  }

  /**
   * Creation and initialisation of a single test case from a definition.
   *
   * @param testCaseDefinition Definition of a single test case {@link TestCaseDefinition}
   * @return a new test case {@link TestCase}
   */
  public TestCase generateTestCase(TestCaseDefinition testCaseDefinition) {
    validateTestDefinition(testCaseDefinition);
    String testName = testCaseDefinition.getTitle();

    List<Mock> mocks = getMocks(testName, testCaseDefinition);

    TestCase testCase =
        new TestCase(
            testName,
            mocks,
            testCaseValidator,
            executionStatusFactory.generateTestReport(testCaseDefinition));

    Exchange exchange = parseExchangeProperties(testCaseDefinition.getWhenExecute(), camelContext);

    try {
      RouteInvoker invoker = routeInvokerFactory.getInstance(exchange);
      testCase.setExecutionWrapper(new ExecutionWrapper(testName, exchange, invoker));
    } catch (NoRouteInvokerException e) {
      testCase.reportExecutionException(e);
    }
    return testCase;
  }

  private List<Mock> getMocks(String testName, TestCaseDefinition testCaseDefinition) {
    return testCaseDefinition.getWithMocks().stream()
        .map(
            connectionProperties ->
                mockFactory.newMockInstance(
                    testName, parseExchangeProperties(connectionProperties, camelContext)))
        .collect(toList());
  }

  private void validateTestDefinition(TestCaseDefinition testCaseDefinition) {
    if (testCaseDefinition.getWhenExecute() == null) {
      throw new IllegalArgumentException("When-execute is not defined!");
    }
  }

  private void validateTestCaseInitializations(List<TestCase> testCases) {
    if (testCases.size() != testCaseBatchDefinition.getTestCaseDefinitions().size()) {
      throw new BeanCreationException("Some test cases were not created.");
    }
  }
}
