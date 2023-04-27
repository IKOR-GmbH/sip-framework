package de.ikor.sip.foundation.testkit.configurationproperties;

import static de.ikor.sip.foundation.testkit.config.TestCasesConfig.*;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.testkit.configurationproperties.models.EndpointProperties;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

/** Batch test definition. List of {@link TestCaseDefinition} objects. */
@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties
@EnableConfigurationProperties
public class TestCaseBatchDefinition implements Validator {

  @Valid private List<TestCaseDefinition> testCaseDefinitions = new ArrayList<>();

  @Override
  public boolean supports(Class<?> clazz) {
    return TestCaseBatchDefinition.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    TestCaseBatchDefinition testCaseBatchDefinition = (TestCaseBatchDefinition) target;
    for (TestCaseDefinition definition : testCaseBatchDefinition.getTestCaseDefinitions()) {
      String testName = definition.getTitle();

      if (testName.isEmpty()) {
        throw new SIPFrameworkException("Test title is missing");
      }

      if (definition.getWhenExecute() == null) {
        throw SIPFrameworkException.init("when-execute section is required for test: %s", testName);
      }

      validateEndpointIdAndConnectorIdFields(definition.getWhenExecute(), testName, WHEN_EXECUTE);
      definition
          .getWithMocks()
          .forEach(
              properties ->
                  validateEndpointIdAndConnectorIdFields(properties, testName, WITH_MOCKS));
      definition
          .getThenExpect()
          .forEach(
              properties ->
                  validateEndpointIdAndConnectorIdFields(properties, testName, THEN_EXPECT));
    }
  }

  private void validateEndpointIdAndConnectorIdFields(
      EndpointProperties properties, String testName, String definitionPart) {
    if (properties.getEndpointId() == null && properties.getConnectorId() == null) {
      throw SIPFrameworkException.init(
          "Parameter endpointId or connectorId is missing in %s for test: %s (one of them should be specified)",
          definitionPart, testName);
    }
    if (properties.getEndpointId() != null && properties.getConnectorId() != null) {
      throw SIPFrameworkException.init(
          "Both endpointId and connectorId parameters are defined in %s for test: %s (only one is allowed)",
          definitionPart, testName);
    }
  }
}
