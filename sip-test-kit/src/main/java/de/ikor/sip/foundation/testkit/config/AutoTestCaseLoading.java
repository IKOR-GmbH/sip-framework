package de.ikor.sip.foundation.testkit.config;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
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

  private static final String YML_TEST_CASES_PATH_PROPERTY = "sip.testkit.test-cases-path";
  private static final String TEST_CASES_PROPERTIES_NAME = "TestCasesProperties";

  /** Adds testcases to environment */
  @Bean
  public PropertySourcesPlaceholderConfigurer testKitTests(ConfigurableEnvironment environment) {
    String testCasePath = environment.getProperty(YML_TEST_CASES_PATH_PROPERTY);
    Properties testCasesFromFile = loadTestsFromYMLPropertiesFile(testCasePath);
    PropertiesPropertySource testCasesPropertySource =
        new PropertiesPropertySource(TEST_CASES_PROPERTIES_NAME, testCasesFromFile);
    environment.getPropertySources().addLast(testCasesPropertySource);
    return new PropertySourcesPlaceholderConfigurer();
  }

  private static Properties loadTestsFromYMLPropertiesFile(String path) {
    YamlPropertiesFactoryBean yamlPropertiesFactory = new YamlPropertiesFactoryBean();
    yamlPropertiesFactory.setResources(getResources(path));
    return Objects.requireNonNull(yamlPropertiesFactory.getObject());
  }

  private static Resource[] getResources(String path) {
    ClassLoader classLoader = AutoTestCaseLoading.class.getClassLoader();
    ResourcePatternResolver resourcePatternResolver =
        new PathMatchingResourcePatternResolver(classLoader);
    try {
      return resourcePatternResolver.getResources(path);
    } catch (IOException e) {
      throw new BeanInitializationException("File not found for path " + path);
    }
  }
}
