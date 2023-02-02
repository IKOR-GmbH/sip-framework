package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import org.apache.camel.model.RouteDefinition;
import org.springframework.beans.factory.annotation.Autowired;

abstract class AnnotatedEndpoint implements Orchestrator<EndpointOrchestrationInfo> {
  private DeclarationsRegistry declarationsRegistry;

  protected final DeclarationsRegistry getDeclarationsRegistry() {
    return declarationsRegistry;
  }

  @Autowired
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

  @Override
  public void doAfter(final EndpointOrchestrationInfo data) {
    configureAfterResponse(data.getRouteDefinition());
  }

  protected void configureEndpointRoute(final RouteDefinition definition) {
    // NO-OP by default
  }

  protected void configureAfterResponse(final RouteDefinition definition) {
    // NO-OP by default
  }
}
