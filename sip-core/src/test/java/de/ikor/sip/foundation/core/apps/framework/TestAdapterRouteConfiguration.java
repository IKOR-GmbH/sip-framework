package de.ikor.sip.foundation.core.apps.framework;

import static org.apache.camel.builder.Builder.simple;

import de.ikor.sip.foundation.core.framework.AdapterRouteConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestAdapterRouteConfiguration extends AdapterRouteConfiguration {

  public static final String GLOBAL_HEADER_KEY = "global";
  public static final String GLOBAL_HEADER_VALUE = "global config";

  @Override
  public void globalConfiguration() {
    configuration().interceptFrom().setHeader(GLOBAL_HEADER_KEY, simple(GLOBAL_HEADER_VALUE));
  }
}
