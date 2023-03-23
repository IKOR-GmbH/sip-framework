package de.ikor.sip.foundation.testkit.config;

import static de.ikor.sip.foundation.testkit.util.TestKitHelper.parseExchangeProperties;

import de.ikor.sip.foundation.core.declarative.RouteRole;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import de.ikor.sip.foundation.testkit.configurationproperties.TestCaseBatchDefinition;
import de.ikor.sip.foundation.testkit.configurationproperties.TestCaseDefinition;
import de.ikor.sip.foundation.testkit.configurationproperties.models.EndpointProperties;
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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
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
  private final Optional<RoutesRegistry> routesRegistry;

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

    routesRegistry.ifPresent(registry -> initTestCaseDefinitionEndpoints(testCaseDefinition));

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

  private void initTestCaseDefinitionEndpoints(TestCaseDefinition definition) {
    if (definition.getWhenExecute().getConnector() != null) {
      replaceConnectorIdsWithRouteIds(definition.getWhenExecute());
    }
    definition.getWithMocks().forEach(this::setEndpointBasedOnConnectorId);
  }

  private void replaceConnectorIdsWithRouteIds(EndpointProperties whenExecute) {
    String connectorId = whenExecute.getConnector();
    whenExecute.setEndpoint(
        routesRegistry
            .get()
            .getRouteIdByConnectorIdAndRole(
                connectorId, RouteRole.CONNECTOR_REQUEST_ORCHESTRATION));
  }

  private void setEndpointBasedOnConnectorId(EndpointProperties properties) {
    if (properties.getConnector() != null) {
      properties.setEndpoint(
          routesRegistry
              .get()
              .getRouteIdByConnectorIdAndRole(
                  properties.getConnector(), RouteRole.EXTERNAL_ENDPOINT));
    }
  }

  private List<Mock> getMocks(String testName, TestCaseDefinition testCaseDefinition) {
    return testCaseDefinition.getWithMocks().stream()
        .map(
            connectionProperties ->
                mockFactory.newMockInstance(
                    testName, parseExchangeProperties(connectionProperties, camelContext)))
        .toList();
  }

  private void validateTestDefinition(TestCaseDefinition testCaseDefinition) {
    if (testCaseDefinition.getWhenExecute() == null) {
      throw new SIPFrameworkException("When-execute is not defined!");
    }
    validateEndpointAndConnectorFields(testCaseDefinition.getWhenExecute());
    testCaseDefinition.getWithMocks().forEach(this::validateEndpointAndConnectorFields);
    testCaseDefinition.getThenExpect().forEach(this::validateConnectorField);
  }

  private void validateEndpointAndConnectorFields(EndpointProperties properties) {
    if (properties.getEndpoint() != null && properties.getConnector() != null) {
      throw new SIPFrameworkException(
          "Both endpoint and connector fields are defined in When-execute!");
    }
  }

  private void validateConnectorField(EndpointProperties properties) {
    if (properties.getConnector() != null) {
      throw new SIPFrameworkException(
          "Connector field is not allowed out of When-execute definition!");
    }
  }

  private void validateTestCaseInitializations(List<TestCase> testCases) {
    if (testCases.size() != testCaseBatchDefinition.getTestCaseDefinitions().size()) {
      throw new SIPFrameworkInitializationException("Some test cases were not created.");
    }
  }
}
