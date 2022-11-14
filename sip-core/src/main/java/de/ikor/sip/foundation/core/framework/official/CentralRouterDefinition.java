package de.ikor.sip.foundation.core.framework.official;

import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.routers.CDMValidator;
import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.CentralRouterDomainModel;
import de.ikor.sip.foundation.core.framework.routers.UseCaseTopologyDefinition;
import lombok.Getter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

import java.util.*;

import static java.lang.String.format;

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
    Class<?> expectedRequestClass;
    if (this.getClass().isAnnotationPresent(CentralRouterDomainModel.class)) {
      expectedRequestClass =
          this.getClass().getAnnotation(CentralRouterDomainModel.class).requestType();
    } else {
      expectedRequestClass = String.class;
    }
    return expectedRequestClass;
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
