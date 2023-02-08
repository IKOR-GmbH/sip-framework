package de.ikor.sip.foundation.core.declarative.endpoints;

import static de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper.formatEndpointId;

import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.camel.model.RouteDefinition;

public abstract class AnnotatedInboundEndpoint extends AnnotatedEndpoint
    implements InboundEndpointDefinition {

  private final InboundEndpoint inboundEndpointAnnotation =
      DeclarativeHelper.getAnnotationOrThrow(InboundEndpoint.class, this);

  private final String endpointId =
      StringUtils.defaultIfEmpty(
          inboundEndpointAnnotation.endpointId(),
          formatEndpointId(getEndpointType(), getScenarioId(), getConnectorId()));

  @Override
  public String getEndpointId() {
    return endpointId;
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
  public void configureAfterResponse(final RouteDefinition definition) {}
}
