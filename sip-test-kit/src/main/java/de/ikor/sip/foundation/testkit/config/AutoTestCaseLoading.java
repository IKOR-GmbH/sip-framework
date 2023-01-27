package de.ikor.sip.foundation.testkit.config;

import static de.ikor.sip.foundation.testkit.SIPBatchTest.SIP_BATCH_TEST;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
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
  private static final String DEFAULT_TEST_CASES_LOCATION = "test-case-definition.yml";
  private static final String TEST_CASES_PROPERTIES_NAME = "TestCasesProperties";
  private static final String ROUTE_CONTROLLER_SUPERVISE =
      "camel.springboot.routeControllerSuperviseEnabled";

  /** Adds testcases to environment */
  @Bean("testKitBatchTests")
  public PropertySourcesPlaceholderConfigurer prepareTestingEnvironment(
      ConfigurableEnvironment environment) {
    String testCasePath =
        environment.getProperty(YML_TEST_CASES_PATH_PROPERTY, DEFAULT_TEST_CASES_LOCATION);
    if (isBatchTest(environment)) {
      addRouteControllerSuperviseProperty(environment);
    }
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
      throw new SIPFrameworkInitializationException("File not found for path " + path);
    }
  }

  private void addRouteControllerSuperviseProperty(ConfigurableEnvironment environment) {
    MutablePropertySources propertySources = environment.getPropertySources();
    propertySources.addFirst(
        new MapPropertySource("sipBatchProperties", Map.of(ROUTE_CONTROLLER_SUPERVISE, true)));
  }

  private boolean isBatchTest(ConfigurableEnvironment environment) {
    return environment.getProperty(SIP_BATCH_TEST, "false").equals("true");
  }
}
