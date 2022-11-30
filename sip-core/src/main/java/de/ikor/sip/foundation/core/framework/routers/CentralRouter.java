package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.GlobalRoutesConfiguration;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousDummyRouteConfigurationBuilder;

class CentralRouter {
  private final CentralRouterDefinition routerDefinition;
  private final Scenario scenario;

  public CentralRouter(CentralRouterDefinition routerDefinition) {
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
    InConnectorsRouteBinder inConnectorsRouteBinder = new InConnectorsRouteBinder(scenario);
    inConnectorsRouteBinder.bindInConnectors(convert(routerDefinition.getInConnectorDefinitions()));
  }

  private void buildOnException() {
    routerDefinition.configureOnException();
  }

  private void buildConfiguration() {
    routerDefinition.setRouteConfigurationBuilder(this.scenario.getScenarioRoutesConfiguration());
    routerDefinition.defineConfiguration();
  }

  private void bindOutConnectors() {
    OutConnectorsRouteBinder outConnectorsBinder = new OutConnectorsRouteBinder(scenario);

    outConnectorsBinder.appendOutConnectorsSeq(
        routerDefinition.getDefinition().getConnectorsBindInSequence());
    outConnectorsBinder.appendOutConnectorsParallel(
        routerDefinition.getDefinition().getConnectorsBindInParallel());
  }

  void appendToRouteConfig(Optional<GlobalRoutesConfiguration> globalRoutesConfiguration) {
    scenario.setScenarioRoutesConfiguration(anonymousDummyRouteConfigurationBuilder());
    globalRoutesConfiguration.ifPresent(scenario::copyGlobalToScenarioConfiguration);
  }

  private List<InConnector> convert(List<InConnectorDefinition> inConnectorDefinitions) {
    return inConnectorDefinitions.stream().map(InConnector::new).collect(Collectors.toList());
  }
}
