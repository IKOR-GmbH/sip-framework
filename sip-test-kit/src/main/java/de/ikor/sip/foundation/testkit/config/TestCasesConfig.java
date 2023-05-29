package de.ikor.sip.foundation.testkit.config;

import static de.ikor.sip.foundation.testkit.util.TestKitHelper.parseExchangeProperties;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.RouteRole;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorType;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.testkit.configurationproperties.TestCaseBatchDefinition;
import de.ikor.sip.foundation.testkit.configurationproperties.TestCaseDefinition;
import de.ikor.sip.foundation.testkit.configurationproperties.models.EndpointProperties;
import de.ikor.sip.foundation.testkit.exception.NoRouteInvokerException;
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

  public static final String WHEN_EXECUTE = "when-execute";
  public static final String WITH_MOCKS = "with-mocks";
  public static final String THEN_EXPECT = "then-expect";

  private final RouteInvokerFactory routeInvokerFactory;
  private final MockFactory mockFactory;
  private final CamelContext camelContext;
  private final TestCaseValidator testCaseValidator;
  private final TestExecutionStatusFactory executionStatusFactory;
  private final Optional<TestCaseBatchDefinition> testCaseBatchDefinition;
  private final TestCaseCollector testCaseCollector;
  private final Optional<RoutesRegistry> routesRegistry;

  private final Optional<DeclarationsRegistryApi> declarationsRegistry;

  /** Creates test cases based on batch test cases definition. */
  @EventListener(ApplicationReadyEvent.class)
  public void generateTestCases() {
    List<TestCase> testCases = new LinkedList<>();
    if (testCaseBatchDefinition.isPresent()) {
      for (TestCaseDefinition testCaseDefinition :
          testCaseBatchDefinition.get().getTestCaseDefinitions()) {
        testCases.add(generateTestCase(testCaseDefinition));
      }
    }
    testCaseCollector.setTestCases(testCases);
  }

  /**
   * Creation and initialisation of a single test case from a definition.
   *
   * @param testCaseDefinition Definition of a single test case {@link TestCaseDefinition}
   * @return a new test case {@link TestCase}
   */
  public TestCase generateTestCase(TestCaseDefinition testCaseDefinition) {
    String testName = testCaseDefinition.getTitle();

    declarationsRegistry.ifPresent(registry -> validateConnectors(testCaseDefinition));
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
    setEndpointBasedOnConnectorId(
        definition.getWhenExecute(), RouteRole.CONNECTOR_REQUEST_ORCHESTRATION);
    definition
        .getWithMocks()
        .forEach(
            properties -> setEndpointBasedOnConnectorId(properties, RouteRole.EXTERNAL_ENDPOINT));
    definition
        .getThenExpect()
        .forEach(
            properties -> setEndpointBasedOnConnectorId(properties, RouteRole.EXTERNAL_ENDPOINT));
  }

  private void setEndpointBasedOnConnectorId(EndpointProperties properties, RouteRole role) {
    if (properties.getConnectorId() != null) {
      String routeId =
          routesRegistry.get().getRouteIdByConnectorIdAndRole(properties.getConnectorId(), role);
      if (routeId == null) {
        throw SIPFrameworkException.init(
            "There is no connector with id %s", properties.getConnectorId());
      }
      properties.setEndpointId(routeId);
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

  private void validateConnectors(TestCaseDefinition testCaseDefinition) {
    validateConnectorType(testCaseDefinition.getWhenExecute(), ConnectorType.IN, WHEN_EXECUTE);
    testCaseDefinition
        .getWithMocks()
        .forEach(properties -> validateConnectorType(properties, ConnectorType.OUT, WITH_MOCKS));
    testCaseDefinition
        .getThenExpect()
        .forEach(properties -> validateConnectorType(properties, ConnectorType.OUT, THEN_EXPECT));
  }

  private void validateConnectorType(
      EndpointProperties properties, ConnectorType type, String definitionPart) {
    String connectorId = properties.getConnectorId();
    Optional<ConnectorDefinition> connectorOpt =
        declarationsRegistry.flatMap(registry -> registry.getConnectorById(connectorId));
    if (connectorId != null && connectorOpt.isPresent()) {
      ConnectorDefinition connector = connectorOpt.get();
      if (!connector.getConnectorType().equals(type)) {
        throw SIPFrameworkException.init(
            "Connector id %s with wrong connector type (%s) used in %s. Use connector with type: (%s)",
            connectorId, connector.getConnectorType(), definitionPart, type);
      }
    }
  }
}
