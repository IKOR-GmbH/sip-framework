package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.RouteConfigurationDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CentralRouter {
  private final List<InConnector> inConnectors = new ArrayList<>();

  @Getter private UseCaseTopologyDefinition definition;
  @Setter private RouteConfigurationBuilder routeConfigurationBuilder;

  public abstract void defineTopology() throws Exception;

  public void configureOnException() {}

  public void defineConfiguration() {}

  protected RouteConfigurationDefinition configuration() {
    return routeConfigurationBuilder.routeConfiguration();
  }

  public UseCaseTopologyDefinition input(InConnector... inConnectors) {
    this.inConnectors.addAll(Arrays.asList(inConnectors));
    definition = new UseCaseTopologyDefinition();
    return definition;
  }

  String getScenario() {
    return this.getClass().getAnnotation(IntegrationScenario.class).name();
  }

  public Class<?> getCentralModelRequestClass() {
    return this.getClass().isAnnotationPresent(IntegrationScenario.class)
        ? this.getClass().getAnnotation(IntegrationScenario.class).requestType()
        : String.class;
  }

  public Class<?> getCentralModelResponseClass() {
    if (this.getClass().isAnnotationPresent(IntegrationScenario.class)) {
      return this.getClass().getAnnotation(IntegrationScenario.class).responseType();
    }
    throw new IllegalStateException(""); // todo add message
  }

  List<InConnector> getInConnectors() {
    return inConnectors;
  }

  protected CentralRouterService toCentralRouter() {
    return new CentralRouterService(this);
  }
}
