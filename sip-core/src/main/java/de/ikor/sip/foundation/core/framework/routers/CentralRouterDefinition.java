package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

public abstract class CentralRouterDefinition {
  private final List<InConnectorDefinition> inConnectorDefinitions = new ArrayList<>();
  @Getter private UseCaseTopologyDefinition definition;

  public abstract String getScenario();

  public abstract void defineTopology() throws Exception;

  public void configureOnException() {}

  public UseCaseTopologyDefinition input(InConnectorDefinition... inConnectorDefinitions) {
    this.inConnectorDefinitions.addAll(Arrays.asList(inConnectorDefinitions));
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

  public List<InConnectorDefinition> getInConnectorDefinitions() {
    return inConnectorDefinitions;
  }

  protected CentralRouter toCentralRouter() {
    return new CentralRouter(this);
  }
}
