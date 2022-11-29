package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.GlobalRoutesConfiguration;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.camel.model.RouteDefinition;
import lombok.Getter;
import org.apache.camel.builder.RouteConfigurationBuilder;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.*;

@RequiredArgsConstructor
class CentralRouter {
  private final CentralRouterDefinition routerDefinition;
  private Optional<GlobalRoutesConfiguration> routeGlobalConfig;
  private RouteConfigurationBuilder routeConfigurationBuilder;

  @SneakyThrows
  public void setUpRoutes() {
    routerDefinition.defineTopology();
    this.buildActiveRoutes();
  }

  void buildActiveRoutes() {
    this.buildOnException();
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
    return inConnectorDefinitions.stream()
            .map(InConnector::new)
            .collect(Collectors.toList());
  }

  private void configure(InConnector inConnector) {
    inConnector.initDefinition();
    inConnector.configureOnException();
    inConnector.configure();
    appendSipMCAndRouteId(inConnector.getConnectorRouteDefinition(), inConnector.getName());
//    appendCDMValidatorIfResponseIsExpected(inConnector.getConnectorRouteDefinition());
    //TODO fix validator first

    inConnector.handleResponse(inConnector.getConnectorRouteDefinition());
  }

  void appendSipMCAndRouteId(RouteDefinition routeDefinition, String connectorName) {
    routeDefinition
        .to("sipmc:" + routerDefinition.getScenario())
        // TODO double id set
        .routeId(generateRouteId(routerDefinition.getScenario(), connectorName));
  }

  public String getScenario() {
    return routerDefinition.getScenario();
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

  public UseCaseTopologyDefinition getOutTopologyDefinition() {
    return routerDefinition.getDefinition();
  }

  private void appendCDMValidatorIfResponseIsExpected(RouteDefinition connectorRouteDefinition) {
    Class<?> centralModelResponseClass = routerDefinition.getCentralModelResponseClass();
    connectorRouteDefinition.process(new CDMValidator(centralModelResponseClass));
  }

  private void bindOutConnectors() {
    RouteBinder routeBinder =
            new RouteBinder(this.getScenario(), routerDefinition.getCentralModelRequestClass(), routeConfigurationBuilder);
    buildConfiguration(routeConfigurationBuilder);
    routeBinder.appendOutConnectorsSeq(this.getOutTopologyDefinition().getConnectorsBindInSequence());
    routeBinder.appendOutConnectorsParallel(this.getOutTopologyDefinition().getConnectorsBindInParallel());
  }

  public void setGlobalRouteConfig(Optional<GlobalRoutesConfiguration> routeConfiguration) {
    routeConfigurationBuilder =
            routeConfiguration
                    .map(GlobalRoutesConfiguration::getConfigurationBuilder)
                    .orElse(anonymousDummyRouteConfigurationBuilder());
    this.routeGlobalConfig = routeConfiguration;
  }
}
