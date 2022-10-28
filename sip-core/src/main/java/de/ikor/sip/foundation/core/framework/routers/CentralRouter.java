package de.ikor.sip.foundation.core.framework.routers;

import static java.lang.String.format;

import de.ikor.sip.foundation.core.framework.AdapterRouteConfiguration;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.RouteConfigurationDefinition;

public abstract class CentralRouter {
  @Getter @Setter private static CamelContext camelContext;

  private final List<InConnector> inConnectors = new ArrayList<>();

  private UseCaseTopologyDefinition definition;
  @Getter private RouteConfigurationBuilder configuration;

  public abstract String getScenario();

  public abstract void configure() throws Exception;

  public void scenarioConfiguration() {}

  public void configureOnException() {}

  public UseCaseTopologyDefinition from(InConnector... inConnectors) {
    for (InConnector connector : inConnectors) {
      connector.setConfiguration(configuration);
      connector.configureOnException();
      connector.configure();
      appendToSIPmcAndRouteId(connector);
      connector.handleResponse(connector.getConnectorDefinition());
    }
    this.inConnectors.addAll(Arrays.asList(inConnectors));
    definition = new UseCaseTopologyDefinition(camelContext, this.getScenario());
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
    connector.setConfiguration(configuration);
    connector.configureOnException();
    connector.configure();
    appendToSIPmcAndRouteId(connector, "-testing");
    connector.handleResponse(connector.getConnectorDefinition());
  }

  List<InConnector> getInConnectors() {
    return inConnectors;
  }

  UseCaseTopologyDefinition getUseCaseDefinition() {
    return definition;
  }

  private void appendToSIPmcAndRouteId(InConnector connector, String routeSuffix) {
    connector
        .getConnectorDefinition()
        .to("sipmc:" + this.getScenario() + routeSuffix)
        .routeId(generateRouteId(this.getScenario(), connector.getName(), routeSuffix));
  }

  private void appendToSIPmcAndRouteId(InConnector connector) {
    appendToSIPmcAndRouteId(connector, "");
  }

  public static RouteBuilder anonymousDummyRouteBuilder() {
    return new RouteBuilder() {
      @Override
      public void configure() {
        // no need for implementation; used for building routes
      }
    };
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
}
