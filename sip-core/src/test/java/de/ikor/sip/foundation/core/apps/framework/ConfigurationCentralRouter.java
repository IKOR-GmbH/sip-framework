package de.ikor.sip.foundation.core.apps.framework;

import static org.apache.camel.builder.Builder.simple;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.stubs.ConfigInConnector;
import de.ikor.sip.foundation.core.framework.stubs.ConfigOutConnector;

public class ConfigurationCentralRouter extends CentralRouter {

  public static final String SCENARIO_HEADER_KEY = "scenario";
  public static final String SCENARIO_HEADER_VALUE = "scenario header";

  @Override
  public String getScenario() {
    return "config-scenario";
  }

  @Override
  public void configure() throws Exception {
    from(new ConfigInConnector()).to(new ConfigOutConnector());
  }

  @Override
  public void scenarioConfiguration() {
    configuration().interceptFrom().setHeader(SCENARIO_HEADER_KEY, simple(SCENARIO_HEADER_VALUE));
  }

  @Override
  public void configureOnException() {}
}
