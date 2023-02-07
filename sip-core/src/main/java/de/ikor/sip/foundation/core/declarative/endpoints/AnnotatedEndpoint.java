package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import lombok.Data;
import org.apache.camel.model.RouteDefinition;

@Data
abstract class AnnotatedEndpoint
    implements Orchestrator<EndpointOrchestrationInfo>, AnnotatedEndpointType {

  protected static final String ENDPOINT_ID_FORMAT = "%s-%s-%s";
  private DeclarationsRegistry declarationsRegistry;

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
