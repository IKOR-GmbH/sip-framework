package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CentralRouterDefinition {
  private final List<InConnector> inConnectors = new ArrayList<>();
  @Getter private UseCaseTopologyDefinition definition;

  public abstract String getScenario();

  public abstract void defineTopology() throws Exception;

  public void configureOnException() {}

  public UseCaseTopologyDefinition input(InConnector... inConnectors) {
    this.inConnectors.addAll(Arrays.asList(inConnectors));
    definition = new UseCaseTopologyDefinition();
    return definition;
  }

  public Class<?> getCentralModelRequestClass() {
    return this.getClass().isAnnotationPresent(CentralRouterDomainModel.class)
        ? this.getClass().getAnnotation(CentralRouterDomainModel.class).requestType()
        : String.class;
  }

  public Class<?> getCentralModelResponseClass() {
    if (this.getClass().isAnnotationPresent(CentralRouterDomainModel.class)) {
      return this.getClass().getAnnotation(CentralRouterDomainModel.class).responseType();
    }
    throw new IllegalStateException(""); // todo add message
  }

  public List<InConnector> getInConnectors() {
    return inConnectors;
  }

  public CentralRouter toCentralRouter() {
    return new CentralRouter(this);
  }
}
