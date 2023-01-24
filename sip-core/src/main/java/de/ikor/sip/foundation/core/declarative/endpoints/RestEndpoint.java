package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestation.RestEndpointBridgeInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;

public abstract class RestEndpoint extends AnnotatedEndpoint implements InEndpointDefinition {

  @Override
  public void doBridge(final EndpointOrchestrationInfo data) {
    configureRest(((RestEndpointBridgeInfo) data).getRestDefinition());
  }

  @Override
  public Orchestrator<EndpointOrchestrationInfo> getOrchestrator() {
    return this;
  }

  @Override
  public void doOrchestrate(final EndpointOrchestrationInfo data) {
    configureEndpointRoute(data.getRouteDefinition());
  }

  @Override
  public boolean canOrchestrate(final EndpointOrchestrationInfo data) {
    return data != null;
  }

  protected abstract void configureRest(final RestDefinition definition);

  @Override
  protected abstract void configureEndpointRoute(final RouteDefinition definition);

  private final InboundEndpoint inboundEndpointAnnotation =
      ReflectionHelper.getAnnotationOrThrow(InboundEndpoint.class, this);

  public final ConnectorDefinition getConnector() {
    return getDeclarationsRegistry()
        .getConnectorById(inboundEndpointAnnotation.belongsToConnector());
  }

  public final IntegrationScenarioDefinition getProvidedScenario() {
    return getDeclarationsRegistry()
        .getScenarioById(inboundEndpointAnnotation.providesToScenario());
  }
}
