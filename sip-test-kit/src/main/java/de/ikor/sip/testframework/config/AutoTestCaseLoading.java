package de.ikor.sip.testframework.config;

import java.io.IOException;
import java.util.Objects;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/** Loads a batch of test cases from a test case file */
@Configuration
@EnableConfigurationProperties
@Profile("test")
public class AutoTestCaseLoading {

  private static final String TEST_CASES_LOCATION = "testCases.path";
  private static final String DEFAULT_TEST_CASES_LOCATION = "test-case-definition.yml";
  private static final String TEST_CASES_PROPERTIES_NAME = "TestCasesProperties";

  /** Adds testcases to environment */
  @Bean
  public PropertySourcesPlaceholderConfigurer setProperties(Environment environment) {
    String testCasesLocation =
        environment.getProperty(TEST_CASES_LOCATION, DEFAULT_TEST_CASES_LOCATION);

    addTestCasesToPropertySources((ConfigurableEnvironment) environment, testCasesLocation);

    return new PropertySourcesPlaceholderConfigurer();
  }

  private void addTestCasesToPropertySources(
      ConfigurableEnvironment environment, String testCasesLocation) {
    YamlPropertiesFactoryBean yamlPropertiesFactory = new YamlPropertiesFactoryBean();
    yamlPropertiesFactory.setResources(getResources(testCasesLocation));
    PropertiesPropertySource yamlPropertySource =
        new PropertiesPropertySource(
            TEST_CASES_PROPERTIES_NAME, Objects.requireNonNull(yamlPropertiesFactory.getObject()));
    environment.getPropertySources().addLast(yamlPropertySource);
  }

  private Resource[] getResources(String path) {
    ClassLoader classLoader = this.getClass().getClassLoader();
    ResourcePatternResolver resourcePatternResolver =
        new PathMatchingResourcePatternResolver(classLoader);
    try {
      return resourcePatternResolver.getResources(path);
    } catch (IOException e) {
      throw new BeanInitializationException("File not found for path " + path);
    }
  }
}
