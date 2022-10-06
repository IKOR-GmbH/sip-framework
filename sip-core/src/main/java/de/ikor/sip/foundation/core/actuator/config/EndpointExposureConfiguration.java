package de.ikor.sip.foundation.core.actuator.config;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/** Collects all endpoint that should be exposed in actuator */
public class EndpointExposureConfiguration
    implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  private static final String EXPOSURE_INCLUDE = "management.endpoints.web.exposure.include";
  private static final String SIP_DEFAULTS = "sip.core.actuator.endpoints";

  @Override
  public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    ConfigurableEnvironment environment = event.getEnvironment();
    Properties exposedEndpoints = new Properties();
    Properties loadedProperties = loadDefaultProperties();
    String endpoints = resolveEndpoints(environment, loadedProperties);
    exposedEndpoints.put(EXPOSURE_INCLUDE, endpoints);
    PropertiesPropertySource endpointsPropertySource =
        new PropertiesPropertySource("exposedEndpoints", exposedEndpoints);
    environment.getPropertySources().addFirst(endpointsPropertySource);
  }

  private String resolveEndpoints(
      ConfigurableEnvironment environment, Properties loadedProperties) {
    String endpoints = environment.getProperty(EXPOSURE_INCLUDE);
    if (endpoints == null) {
      endpoints = loadedProperties.getProperty(EXPOSURE_INCLUDE);
    }
    if (!"*".equals(endpoints)) {
      String sipEndpoints = loadedProperties.getProperty(SIP_DEFAULTS);
      endpoints = endpoints.concat("," + sipEndpoints);
    }
    return endpoints;
  }

  private Properties loadDefaultProperties() {
    YamlPropertiesFactoryBean yamlPropertiesFactory = new YamlPropertiesFactoryBean();
    yamlPropertiesFactory.setResources(getResources());
    return Objects.requireNonNull(yamlPropertiesFactory.getObject());
  }

  private Resource[] getResources() {
    ClassLoader classLoader = EndpointExposureConfiguration.class.getClassLoader();
    ResourcePatternResolver resourcePatternResolver =
        new PathMatchingResourcePatternResolver(classLoader);
    try {
      return resourcePatternResolver.getResources(getPath());
    } catch (IOException e) {
      throw new IllegalArgumentException("File not found for path " + getPath());
    }
  }

  protected String getPath() {
    return "sip-core-default-config.yaml";
  }
}
