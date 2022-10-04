package de.ikor.sip.foundation.core.actuator.config;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import java.util.Properties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.ClassUtils;

/** Collects all endpoint that should be exposed in actuator */
@Configuration
public class EndpointExposureConfiguration {

  private static final String EXPOSURE_INCLUDE = "management.endpoints.web.exposure.include";

  /**
   * Collects all SIP actuator endpoints and adds them to list of endpoints defined in configuration
   *
   * @param environment {@link ConfigurableEnvironment}
   * @param applicationContext {@link ApplicationContext}
   * @return exposed endpoints
   */
  @Bean({"endpointExposure"})
  public String generateExposedActuatorEndpoints(
      ConfigurableEnvironment environment, ApplicationContext applicationContext) {
    Properties props = new Properties();
    String endpoints = resolveEndpoints(environment, applicationContext);
    props.put(EXPOSURE_INCLUDE, endpoints);
    PropertiesPropertySource testCasesPropertySource =
        new PropertiesPropertySource("exposedEndpoints", props);
    environment.getPropertySources().addFirst(testCasesPropertySource);
    return endpoints;
  }

  private String resolveEndpoints(
      ConfigurableEnvironment environment, ApplicationContext applicationContext) {
    String endpoints = environment.getProperty(EXPOSURE_INCLUDE);
    if (endpoints == null) {
      return "";
    }
    if ("*".equals(endpoints)) {
      return "*";
    }
    for (Object bean : applicationContext.getBeansWithAnnotation(SIPFeature.class).values()) {
      SIPFeature sipFeature = ClassUtils.getUserClass(bean).getAnnotation(SIPFeature.class);
      endpoints = endpoints.concat("," + sipFeature.name());
    }
    return endpoints;
  }
}
