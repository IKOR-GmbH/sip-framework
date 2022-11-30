package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.GlobalRoutesConfiguration;
import de.ikor.sip.foundation.core.framework.connectors.InConnectorService;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousDummyRouteConfigurationBuilder;

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
    InConnectorsRouteBinder inConnectorsRouteBinder = new InConnectorsRouteBinder(scenario);
    inConnectorsRouteBinder.bindInConnectors(convert(routerDefinition.getInConnectors()));
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

  private List<InConnectorService> convert(List<InConnector> inConnectors) {
    return inConnectors.stream().map(InConnectorService::new).collect(Collectors.toList());
  }
}
