package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Endpoint;
import org.apache.camel.model.RouteDefinition;

@Data
abstract class AnnotatedEndpoint implements Orchestrator<EndpointOrchestrationInfo> {

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private DeclarationsRegistry declarationsRegistry;

  private Endpoint camelEndpoint;
  private String endpointId;

  protected final DeclarationsRegistry getDeclarationsRegistry() {
    return declarationsRegistry;
  }

  public final void setDeclarationsRegistry(final DeclarationsRegistry declarationsRegistry) {
    this.declarationsRegistry = declarationsRegistry;
  }

  public Orchestrator<EndpointOrchestrationInfo> getOrchestrator() {
    return this;
  }

  @Override
  public boolean canOrchestrate(final EndpointOrchestrationInfo data) {
    return data != null;
  }

  @Override
  public void doOrchestrate(final EndpointOrchestrationInfo data) {
    configureEndpointRoute(data.getRouteDefinition());
  }

  protected void configureEndpointRoute(final RouteDefinition definition) {
    // NO-OP by default
  }
}
