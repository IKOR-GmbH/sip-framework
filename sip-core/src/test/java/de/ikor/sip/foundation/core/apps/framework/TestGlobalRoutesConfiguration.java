package de.ikor.sip.foundation.core.apps.framework;

import static org.apache.camel.builder.Builder.simple;

import de.ikor.sip.foundation.core.framework.configurations.GlobalRoutesConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestGlobalRoutesConfiguration extends GlobalRoutesConfiguration {

  public static final String GLOBAL_HEADER_KEY = "global";
  public static final String GLOBAL_HEADER_VALUE = "global config";

  @Override
  public void defineGlobalConfiguration() {
    configuration().interceptFrom().setHeader(GLOBAL_HEADER_KEY, simple(GLOBAL_HEADER_VALUE));
  }
}
