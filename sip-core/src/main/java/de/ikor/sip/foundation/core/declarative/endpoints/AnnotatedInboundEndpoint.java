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
  public String getEndpointId() {
    if (inboundEndpointAnnotation.endpointId().isEmpty()) {
      return String.format(
          ENDPOINT_ID_FORMAT, getEndpointType().getValue(), getScenarioId(), getConnectorId());
    }
    return inboundEndpointAnnotation.endpointId();
  }

  @Override
  public final String getConnectorId() {
    return inboundEndpointAnnotation.belongsToConnector();
  }

  @Override
  public final String getScenarioId() {
    return inboundEndpointAnnotation.providesToScenario();
  }

  @Override
  public EndpointType getEndpointType() {
    return EndpointType.IN;
  }
}
