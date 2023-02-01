package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.annonations.OutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;

public abstract class AnnotatedOutboundEndpoint extends AnnotatedEndpoint
    implements OutboundEndpointDefinition {

  private final OutboundEndpoint outboundEndpointAnnotation =
      ReflectionHelper.getAnnotationOrThrow(OutboundEndpoint.class, this);

  @Override
  public final IntegrationScenarioDefinition getConsumedScenario() {
    return getDeclarationsRegistry()
        .getScenarioById(outboundEndpointAnnotation.consumesFromScenario());
  }

  @Override
  public final ConnectorDefinition getConnector() {
    return getDeclarationsRegistry()
        .getConnectorById(outboundEndpointAnnotation.belongsToConnector());
  }

  @Override
  public final String getConnectorId() {
    return this.getClass().getAnnotation(OutboundEndpoint.class).belongsToConnector();
  }
}
