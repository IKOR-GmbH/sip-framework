package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.annonations.OutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;

public abstract class AnnotatedOutboundEndpoint extends AnnotatedEndpoint
    implements OutboundEndpointDefinition {

  private final OutboundEndpoint outboundEndpointAnnotation =
      ReflectionHelper.getAnnotationOrThrow(OutboundEndpoint.class, this);

  @Override
  public String getEndpointId() {
    if (outboundEndpointAnnotation.endpointId().isEmpty()) {
      return String.format(
          ENDPOINT_ID_FORMAT, getEndpointType().getValue(), getScenarioId(), getConnectorId());
    }
    return outboundEndpointAnnotation.endpointId();
  }

  @Override
  public final String getConnectorId() {
    return outboundEndpointAnnotation.belongsToConnector();
  }

  @Override
  public final String getScenarioId() {
    return outboundEndpointAnnotation.consumesFromScenario();
  }

  @Override
  public EndpointType getEndpointType() {
    return EndpointType.OUT;
  }
}
