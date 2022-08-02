package de.ikor.sip.foundation.testkit.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
  private static final String DEFAULT_TEST_CASES_LOCATION = "test-case-definition.yml";
  private static final String TEST_CASES_PROPERTIES_NAME = "TestCasesProperties";
  private static final String SIP_BATCH_TEST = "sip.testkit.batchTest";
  private static final String ROUTE_CONTROLLER_SUPERVISE =
      "camel.springboot.routeControllerSuperviseEnabled";

  /** Adds testcases to environment */
  @Bean
  public PropertySourcesPlaceholderConfigurer testKitTests(ConfigurableEnvironment environment) {
    String testCasePath =
        environment.getProperty(YML_TEST_CASES_PATH_PROPERTY, DEFAULT_TEST_CASES_LOCATION);
    addRouteControllerSuperviseProperty(environment);
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

  private void addProperty(ConfigurableEnvironment environment, String propertyKey, Object value) {
    MutablePropertySources propertySources = environment.getPropertySources();
    Map<String, Object> map = new HashMap<>();
    map.put(propertyKey, value);
    propertySources.addFirst(new MapPropertySource("addedPropertiesMap", map));
  }

  private void addRouteControllerSuperviseProperty(ConfigurableEnvironment environment) {
    if (environment.getProperty(SIP_BATCH_TEST, "false").equals("true")) {
      addProperty(environment, ROUTE_CONTROLLER_SUPERVISE, true);
    }
  }
}
