package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AnnotatedInboundEndpoint extends AnnotatedEndpoint
    implements InboundEndpointDefinition {

  private final InboundEndpoint inboundEndpointAnnotation =
      ReflectionHelper.getAnnotationOrThrow(InboundEndpoint.class, this);

  @Override
  public final ConnectorDefinition getConnector() {
    return getDeclarationsRegistry()
        .getConnectorById(inboundEndpointAnnotation.belongsToConnector());
  }

  @Override
  public final IntegrationScenarioDefinition getProvidedScenario() {
    return getDeclarationsRegistry()
        .getScenarioById(inboundEndpointAnnotation.providesToScenario());
  }

  @Override
  public final String getConnectorId() {
    return this.getClass().getAnnotation(InboundEndpoint.class).belongsToConnector();
  }
}
