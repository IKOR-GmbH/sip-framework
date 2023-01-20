package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestation.RestBridge;
import de.ikor.sip.foundation.core.declarative.orchestation.RestEndpointBridgeInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class RestEndpoint
    implements RestBridge, Orchestrator<EndpointOrchestrationInfo>, RestEndpointDefinition {

  private DeclarationsRegistry declarationsRegistry;

  protected final DeclarationsRegistry getDeclarationsRegistry() {
    return declarationsRegistry;
  }

  @Autowired
  public final void setDeclarationsRegistry(final DeclarationsRegistry declarationsRegistry) {
    this.declarationsRegistry = declarationsRegistry;
  }

  @Override
  public void doBridge(final RestEndpointBridgeInfo data) {
    configureRest(data.getRestDefinition());
  }

  public RestBridge getBridge() {
    return this;
  }

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
