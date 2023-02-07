package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import org.apache.camel.model.RouteDefinition;

abstract class AnnotatedEndpoint
    implements Orchestrator<EndpointOrchestrationInfo>, AnnotatedEndpointType {

  public Orchestrator<EndpointOrchestrationInfo> getOrchestrator() {
    return this;
  }

  public abstract String getEndpointId();

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
