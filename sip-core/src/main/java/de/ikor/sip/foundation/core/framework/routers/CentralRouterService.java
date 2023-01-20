package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousDummyRouteConfigurationBuilder;

import de.ikor.sip.foundation.core.framework.configurations.GlobalRoutesConfiguration;
import java.util.Optional;
import lombok.SneakyThrows;

class CentralRouterService {
  private final CentralRouter routerDefinition;
  private final Scenario scenario;

  public CentralRouterService(CentralRouter routerDefinition) {
    this.scenario = new Scenario(routerDefinition);
    this.routerDefinition = routerDefinition;
  }

  @SneakyThrows
  public void setUpRoutes() {
    routerDefinition.defineTopology();
    this.buildActiveRoutes();
  }

  void buildActiveRoutes() {
    this.buildOnException();
    this.buildConfiguration();
    this.bindInConnectors();
    this.bindOutConnectors();
  }

  private void bindInConnectors() {
    new InConnectorsRouteBinder(scenario).bindInConnectors(routerDefinition.getInConnectors());
  }

  private void buildOnException() {
    routerDefinition.configureOnException();
  }

  private void buildConfiguration() {
    routerDefinition.setRouteConfigurationBuilder(this.scenario.getScenarioRoutesConfiguration());
    routerDefinition.defineConfiguration();
  }

  private void bindOutConnectors() {
    new OutConnectorsRouteBinder(scenario).appendOutConnectors(routerDefinition.getDefinition());
  }

  void appendToRouteConfig(Optional<GlobalRoutesConfiguration> globalRoutesConfiguration) {
    scenario.setScenarioRoutesConfiguration(anonymousDummyRouteConfigurationBuilder());
    globalRoutesConfiguration.ifPresent(scenario::copyGlobalToScenarioConfiguration);
  }
}