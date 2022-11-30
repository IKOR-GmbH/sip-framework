package de.ikor.sip.foundation.core.apps.framework;

import static org.apache.camel.builder.Builder.simple;

import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.IntegrationScenario;
import de.ikor.sip.foundation.core.framework.stubs.ConfigInConnector;
import de.ikor.sip.foundation.core.framework.stubs.ConfigOutConnector;

@IntegrationScenario(name = "config-scenario")
public class ConfigurationTestingCentralRouter extends CentralRouter {

  public static final String SCENARIO_HEADER_KEY = "scenario";
  public static final String SCENARIO_HEADER_VALUE = "scenario header";

  @Override
  public void defineTopology() throws Exception {
    input(new ConfigInConnector()).parallelOutput(new ConfigOutConnector("config"));
  }

  @Override
  public void defineConfiguration() {
    configuration().interceptFrom().setHeader(SCENARIO_HEADER_KEY, simple(SCENARIO_HEADER_VALUE));
  }

  @Override
  public void configureOnException() {}
}
