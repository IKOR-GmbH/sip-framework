package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.GlobalRoutesConfiguration;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.RouteConfigurationDefinition;
import org.apache.camel.model.RouteDefinition;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.*;

@RequiredArgsConstructor
class CentralRouter {
  private final CentralRouterDefinition routerDefinition;
  private RouteConfigurationBuilder routeConfigurationBuilder;

  @SneakyThrows
  public void setUpRoutes() {
    routerDefinition.defineTopology();
    this.buildActiveRoutes();
  }

  void buildActiveRoutes() {
    this.buildOnException();
    this.buildConfiguration(routeConfigurationBuilder);
    this.bindInConnectors();
    this.bindOutConnectors();
  }

  private void bindInConnectors() {
    List<InConnector> inConnectors = convert(routerDefinition.getInConnectorDefinitions());
    inConnectors.forEach(inConnector -> inConnector.setConfiguration(routeConfigurationBuilder));
    inConnectors.forEach(this::configure);
    inConnectors.forEach(this::addToContext);
  }

  private List<InConnector> convert(List<InConnectorDefinition> inConnectorDefinitions) {
    return inConnectorDefinitions.stream().map(InConnector::new).collect(Collectors.toList());
  }

  private void configure(InConnector inConnector) {
    inConnector.initDefinition();
    inConnector.configureOnException();
    inConnector.configure();
    appendSipMCAndRouteId(inConnector.getConnectorRouteDefinition(), inConnector.getName());
    inConnector.handleResponse(inConnector.getConnectorRouteDefinition());
  }

  void appendSipMCAndRouteId(RouteDefinition routeDefinition, String connectorName) {
    routeDefinition
        .process(new CDMValidator(getCDMRequestType()))
        .to("sipmc:" + routerDefinition.getScenario())
            .process(new CDMValidator(getCDMResponseType()))
        // TODO double id set; @Nemanja -> can you specify the case
        .routeId(generateRouteId(routerDefinition.getScenario(), connectorName));
  }

  public void buildOnException() {
    routerDefinition.configureOnException();
  }

  public void buildConfiguration(RouteConfigurationBuilder routeConfigurationBuilder) {
    this.routeConfigurationBuilder = routeConfigurationBuilder;
    routerDefinition.setRouteConfigurationBuilder(this.routeConfigurationBuilder);
    routerDefinition.defineConfiguration();
  }

  private void addToContext(InConnector inConnector) {
    try {
      camelContext().addRoutes(inConnector.getRouteBuilder());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Class<?> getCDMRequestType() {
    return routerDefinition.getCentralModelRequestClass();
  }

  private Class<?> getCDMResponseType() {
    return routerDefinition.getCentralModelResponseClass();
  }

  private void bindOutConnectors() {
    RouteBinder routeBinder =
        new RouteBinder(
            routerDefinition.getScenario(),
            routerDefinition.getCentralModelRequestClass(),
            routeConfigurationBuilder);

    routeBinder.appendOutConnectorsSeq(
        routerDefinition.getDefinition().getConnectorsBindInSequence());
    routeBinder.appendOutConnectorsParallel(
        routerDefinition.getDefinition().getConnectorsBindInParallel());
  }

  void setRouteConfig(Optional<GlobalRoutesConfiguration> globalRoutesConfiguration) {
    routeConfigurationBuilder = anonymousDummyRouteConfigurationBuilder();
    globalRoutesConfiguration.ifPresent(this::copyGlobalConfigToRouteConfigurationBuilder);
  }

  private void copyGlobalConfigToRouteConfigurationBuilder(
      GlobalRoutesConfiguration routesConfiguration) {
    List<RouteConfigurationDefinition> routeConfigurations =
        routesConfiguration
            .getConfigurationBuilder()
            .getRouteConfigurationCollection()
            .getRouteConfigurations();

    routeConfigurationBuilder
        .getRouteConfigurationCollection()
        .setRouteConfigurations(routeConfigurations);
  }
}
