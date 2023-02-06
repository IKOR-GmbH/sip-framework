package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.annonations.OutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;

public abstract class AnnotatedOutboundEndpoint extends AnnotatedEndpoint
    implements OutboundEndpointDefinition {

  private final OutboundEndpoint outboundEndpointAnnotation =
      ReflectionHelper.getAnnotationOrThrow(OutboundEndpoint.class, this);

  @Override
  public final String getConnectorId() {
    return this.getClass().getAnnotation(OutboundEndpoint.class).belongsToConnector();
  }

  @Override
  public final String getScenarioId() {
    return this.getClass().getAnnotation(OutboundEndpoint.class).consumesFromScenario();
  }

  @Override
  public EndpointType getEndpointType() {
    return EndpointType.OUTBOUND_ENDPOINT;
  }

  @Override
  public void initEndpointId(String prefix, String scenarioId, String connectorId) {
    endpointId = outboundEndpointAnnotation.endpointId();
    if (endpointId.isEmpty()) {
      endpointId = String.format(ENDPOINT_ID_FORMAT, prefix, scenarioId, connectorId);
    }
  }
}
