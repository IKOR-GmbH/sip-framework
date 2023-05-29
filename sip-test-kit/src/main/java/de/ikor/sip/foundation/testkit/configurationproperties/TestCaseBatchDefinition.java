package de.ikor.sip.foundation.testkit.configurationproperties;

import de.ikor.sip.foundation.testkit.config.AutoBatchTestCaseLoading;
import de.ikor.sip.foundation.testkit.util.TestCaseDefinitionValidator;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

/** Batch test definitions with syntax validation. */
@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties
@EnableConfigurationProperties
@ConditionalOnBean(AutoBatchTestCaseLoading.class)
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
      TestCaseDefinitionValidator.validate(definition);
    }
  }
}
