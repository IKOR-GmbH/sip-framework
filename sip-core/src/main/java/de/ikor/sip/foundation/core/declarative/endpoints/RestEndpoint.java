package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestation.RestEndpointBridgeInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;

public abstract class RestEndpoint extends AnnotatedInboundEndpoint implements InboundEndpointDefinition {

  @Override
  public EndpointConsumerBuilder getInboundEndpoint() {
    return null;
  }

  public void doBridge(final EndpointOrchestrationInfo data) {
    configureRest(((RestEndpointBridgeInfo) data).getRestDefinition());
  }

  @Override
  public void doOrchestrate(final EndpointOrchestrationInfo data) {
    configureEndpointRoute(data.getRouteDefinition());
  }


  protected abstract void configureRest(final RestDefinition definition);

  @Override
  protected abstract void configureEndpointRoute(final RouteDefinition definition);
}
