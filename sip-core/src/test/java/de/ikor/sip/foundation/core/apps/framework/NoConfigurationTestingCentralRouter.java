package de.ikor.sip.foundation.core.apps.framework;

import de.ikor.sip.foundation.core.framework.routers.CentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.routers.CentralRouterDomainModel;
import de.ikor.sip.foundation.core.framework.stubs.ConfigInConnector;
import de.ikor.sip.foundation.core.framework.stubs.NoConfigOutConnector;

import static org.apache.camel.builder.Builder.simple;

@CentralRouterDomainModel
public class NoConfigurationTestingCentralRouter extends CentralRouterDefinition {
  @Override
  public String getScenario() {
    return "no-config-scenario";
  }

  @Override
  public void defineTopology() throws Exception {
    input(ConfigInConnector.withSedaChannel("no-config")).parallelOutput(new NoConfigOutConnector("no-config").outEndpointId("no-config"));
  }

  @Override
  public void defineConfiguration() {
  }

  @Override
  public void configureOnException() {}
}
