package de.ikor.sip.foundation.core.framework.routers;

import static java.lang.String.format;

import de.ikor.sip.foundation.core.framework.AdapterRouteConfiguration;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.RouteConfigurationDefinition;

public abstract class CentralRouter {

  private final List<InConnector> inConnectors = new ArrayList<>();

  private UseCaseTopologyDefinition definition;
  @Getter private RouteConfigurationBuilder configuration;

  public abstract String getScenario();

  public abstract void configure() throws Exception;

  public void scenarioConfiguration() {}

  public void configureOnException() {}

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

  void switchToTestingDefinitionMode(InConnector connector) {
    connector.createNewRouteBuilder();
    connector.setConfiguration(getBuilder());
    connector.configureOnException();
    connector.configure();
    appendToSIPmcAndRouteId(connector, TestingRoutesUtil.TESTING_SUFFIX);
    connector
        .getConnectorRouteDefinition()
        .getOutputs()
        .forEach(TestingRoutesUtil::handleTestIDAppending);
    connector.handleResponse(connector.getConnectorRouteDefinition());
  }

  List<InConnector> getInConnectors() {
    return inConnectors;
  }

  UseCaseTopologyDefinition getUseCaseDefinition() {
    return definition;
  }

  private void appendToSIPmcAndRouteId(InConnector connector, String routeSuffix) {
    connector
        .getConnectorRouteDefinition()
        .to("sipmc:" + this.getScenario() + routeSuffix)
        .routeId(generateRouteId(this.getScenario(), connector.getName(), routeSuffix));
  }

  private void appendToSIPmcAndRouteId(InConnector connector) {
    appendToSIPmcAndRouteId(connector, "");
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
}
