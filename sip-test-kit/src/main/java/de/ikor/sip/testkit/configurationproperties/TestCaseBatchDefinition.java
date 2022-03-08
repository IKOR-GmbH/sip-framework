package de.ikor.sip.testkit.configurationproperties;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Batch test definition. List of {@link TestCaseDefinition} objects. */
@Getter
@Setter
@Configuration
@ConfigurationProperties
@EnableConfigurationProperties
public class TestCaseBatchDefinition {

  private List<TestCaseDefinition> testCaseDefinitions = new ArrayList<>();
}
