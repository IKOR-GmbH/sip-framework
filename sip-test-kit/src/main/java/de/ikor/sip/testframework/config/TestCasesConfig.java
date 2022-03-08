package de.ikor.sip.testframework.config;

import static de.ikor.sip.testframework.workflow.thenphase.result.ValidationType.FULL;
import static java.util.stream.Collectors.toList;

import de.ikor.sip.testframework.configurationproperties.TestCaseBatchDefinition;
import de.ikor.sip.testframework.configurationproperties.TestCaseDefinition;
import de.ikor.sip.testframework.configurationproperties.models.EndpointProperties;
import de.ikor.sip.testframework.exception.handler.ExceptionLogger;
import de.ikor.sip.testframework.workflow.TestCase;
import de.ikor.sip.testframework.workflow.givenphase.Mock;
import de.ikor.sip.testframework.workflow.givenphase.MockFactory;
import de.ikor.sip.testframework.workflow.thenphase.result.ValidationType;
import de.ikor.sip.testframework.workflow.thenphase.validator.TestValidatorFactory;
import de.ikor.sip.testframework.workflow.whenphase.ExecutionWrapper;
import de.ikor.sip.testframework.workflow.whenphase.executor.Executor;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/** Configuration class used for creation of batch test cases based on test definitions. */
@Slf4j
@Component
@RequiredArgsConstructor
public class TestCasesConfig {

  private final Executor executor;
  private final MockFactory mockFactory;
  private final CamelContext camelContext;
  private final TestValidatorFactory validatorFactory;
  private final TestExecutionStatusFactory executionStatusFactory;
  private final TestCaseBatchDefinition testCaseBatchDefinition;

  /**
   * Creates test cases based on batch test cases definition.
   *
   * @return list of {@link TestCase}
   */
  @Bean
  public List<TestCase> generateTestCases() {
    List<TestCase> testCases = new LinkedList<>();
    for (TestCaseDefinition testCaseDefinition : testCaseBatchDefinition.getTestCaseDefinitions()) {
      try {
        testCases.add(generateTestCase(testCaseDefinition, FULL));
      } catch (Exception e) {
        ExceptionLogger.logTestCaseException(e, testCaseDefinition.getTitle());
      }
    }
    validateTestCaseInitializations(testCases);
    return testCases;
  }

  /**
   * Creation and initialisation of a single test case from a definition.
   *
   * @param testCaseDefinition Definition of a single test case {@link TestCaseDefinition}
   * @param validationType type of validation that should be used {@link ValidationType}
   * @return a new test case {@link TestCase}
   */
  public TestCase generateTestCase(
      TestCaseDefinition testCaseDefinition, ValidationType validationType) {
    validateTestDefinition(testCaseDefinition);
    String testName = testCaseDefinition.getTitle();

    List<Mock> mocks =
        testCaseDefinition.getWithMocks().stream()
            .map(
                connectionProperties ->
                    mockFactory.newMockInstance(
                        testName, parseExchangeProperties(connectionProperties)))
            .collect(toList());

    ExecutionWrapper executionWrapper =
        new ExecutionWrapper(
            testName, executor, parseExchangeProperties(testCaseDefinition.getWhenExecute()));

    return new TestCase(
        testName,
        mocks,
        executionWrapper,
        validatorFactory.getValidator(FULL),
        executionStatusFactory.generateTestReport(testCaseDefinition));
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

  private Exchange parseExchangeProperties(EndpointProperties properties) {
    if (properties == null) {
      return null;
    }
    ExchangeBuilder exchangeBuilder =
        ExchangeBuilder.anExchange(camelContext).withBody(properties.getMessage().getBody());
    properties.getMessage().getHeaders().forEach(exchangeBuilder::withHeader);
    exchangeBuilder.withProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, properties.getEndpoint());
    return exchangeBuilder.build();
  }
}
