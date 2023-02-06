package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AnnotatedInboundEndpoint extends AnnotatedEndpoint
    implements InboundEndpointDefinition {

  private final InboundEndpoint inboundEndpointAnnotation =
      ReflectionHelper.getAnnotationOrThrow(InboundEndpoint.class, this);

  @Override
  public final String getConnectorId() {
    return this.getClass().getAnnotation(InboundEndpoint.class).belongsToConnector();
  }

  @Override
  public final String getScenarioId() {
    return this.getClass().getAnnotation(InboundEndpoint.class).providesToScenario();
  }

  @Override
  public EndpointType getEndpointType() {
    return EndpointType.INBOUND_ENDPOINT;
  }

  @Override
  public void initEndpointId(String prefix, String scenarioId, String connectorId) {
    endpointId = inboundEndpointAnnotation.endpointId();
    if (endpointId.isEmpty()) {
      endpointId = String.format(ENDPOINT_ID_FORMAT, prefix, scenarioId, connectorId);
    }
  }
}
