package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.generateRouteId;

import de.ikor.sip.foundation.core.framework.AdapterRouteConfiguration;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.camel.model.RouteDefinition;
import lombok.Getter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.RouteConfigurationDefinition;

@RequiredArgsConstructor
class CentralRouter {
  private final CentralRouterDefinition centralRouterDefinition;
  private List<InConnector> inConnectors;
  private UseCaseTopologyDefinition definition;
  @Getter private RouteConfigurationBuilder configuration;

  public void buildTopology() {
    inConnectors =
        centralRouterDefinition.getInConnectorDefinitions().stream()
            .map(InConnectorDefinition::toInConnector)
            .collect(Collectors.toList());
    inConnectors.forEach(this::configure);
    inConnectors.forEach(this::addToContext);
  }

  private void configure(InConnector inConnector) {
    inConnector.setDefinition();
    inConnector.configureOnException();
    inConnector.configure();
    appendSipMCAndRouteId(inConnector.getConnectorRouteDefinition(), inConnector.getName());
//    appendCDMValidatorIfResponseIsExpected(inConnector.getConnectorRouteDefinition());
    //TODO fix validator first

    inConnector.handleResponse(inConnector.getConnectorRouteDefinition());
  }
  public void scenarioConfiguration() {}

  public void configureOnException() {}

  void appendSipMCAndRouteId(RouteDefinition routeDefinition, String connectorName) {
    routeDefinition
        .to("sipmc:" + centralRouterDefinition.getScenario())
        // TODO double id set
        .routeId(generateRouteId(centralRouterDefinition.getScenario(), connectorName));
  }

  public String getScenario() {
    return centralRouterDefinition.getScenario();
  }

  public Class<?> getCentralModelRequestClass() {
    return centralRouterDefinition.getCentralModelRequestClass();
  }

  public void buildOnException() {
    centralRouterDefinition.configureOnException();
  }

  private void addToContext(InConnector inConnector) {
    try {
      camelContext().addRoutes(inConnector.getRouteBuilder());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public Map<OutConnectorDefinition[], String> getOutTopologyDefinition() {
    return centralRouterDefinition.getDefinition().getAllConnectors();
  }

  private void appendCDMValidatorIfResponseIsExpected(RouteDefinition connectorRouteDefinition) {
    Class<?> centralModelResponseClass = centralRouterDefinition.getCentralModelResponseClass();
    connectorRouteDefinition.process(new CDMValidator(centralModelResponseClass));
  }
  public static RouteBuilder anonymousDummyRouteBuilder(RouteConfigurationBuilder configuration) {
    RouteBuilder routeBuilder =
        new RouteBuilder() {
          @Override
          public void configure() {
            // no need for implementation; used for building routes
          }
        };
    appendConfig(routeBuilder, configuration);
    return routeBuilder;
  }

  public static RouteConfigurationBuilder anonymousDummyRouteConfigurationBuilder() {
    return new RouteConfigurationBuilder() {
      @Override
      public void configuration() {}
    };
  }

  public void addConfigToRouteBuilder(AdapterRouteConfiguration adapterRouteConfiguration) {
    getBuilder()
        .getRouteConfigurationCollection()
        .getRouteConfigurations()
        .addAll(adapterRouteConfiguration.getRouteConfigurationDefinitions());
  }

  private RouteConfigurationBuilder getBuilder() {
    configuration =
        configuration == null ? anonymousDummyRouteConfigurationBuilder() : configuration;
    return configuration;
  }

  private static void appendConfig(
      RouteBuilder routeBuilder, RouteConfigurationBuilder configuration) {
    configuration
        .getRouteConfigurationCollection()
        .getRouteConfigurations()
        .forEach(
            routeConfigurationDefinition -> {
              routeConfigurationDefinition
                  .getIntercepts()
                  .forEach(
                      interceptDefinition ->
                          routeBuilder
                              .getRouteCollection()
                              .getIntercepts()
                              .add(interceptDefinition));
              routeConfigurationDefinition
                  .getInterceptFroms()
                  .forEach(
                      interceptDefinition ->
                          routeBuilder
                              .getRouteCollection()
                              .getInterceptFroms()
                              .add(interceptDefinition));
              routeConfigurationDefinition
                  .getOnCompletions()
                  .forEach(
                      onCompletionDefinition ->
                          routeBuilder
                              .getRouteCollection()
                              .getOnCompletions()
                              .add(onCompletionDefinition));
              routeConfigurationDefinition
                  .getInterceptSendTos()
                  .forEach(
                      interceptDefinition ->
                          routeBuilder
                              .getRouteCollection()
                              .getInterceptSendTos()
                              .add(interceptDefinition));
              routeConfigurationDefinition
                  .getOnExceptions()
                  .forEach(
                      onExceptionDefinition ->
                          routeBuilder
                              .getRouteCollection()
                              .getOnExceptions()
                              .add(onExceptionDefinition));
            });
  }

  public UseCaseTopologyDefinition from(InConnector... inConnectors) {
    for (InConnector connector : inConnectors) {
      connector.setConfiguration(getBuilder());
      connector.configureOnException();
      connector.configure();
      appendToSIPmcAndRouteId(connector);
      connector.handleResponse(connector.getConnectorRouteDefinition());
    }
    this.inConnectors.addAll(Arrays.asList(inConnectors));
    definition = new UseCaseTopologyDefinition(this.getScenario(), getBuilder());
    return definition;
  }

  protected RouteConfigurationDefinition configuration() {
    return getBuilder().routeConfiguration();
  }

  public static String generateRouteId(
          String scenarioName, String connectorName, String routeSuffix) {
    return format("%s-%s%s", scenarioName, connectorName, routeSuffix);
  }
}
