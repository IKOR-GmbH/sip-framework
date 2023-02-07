package de.ikor.sip.foundation.core.declarative.endpoints;

import static de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper.formatEndpointId;

import de.ikor.sip.foundation.core.declarative.annonations.OutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import org.apache.commons.lang3.StringUtils;

public abstract class AnnotatedOutboundEndpoint extends AnnotatedEndpoint
    implements OutboundEndpointDefinition {

  private final OutboundEndpoint outboundEndpointAnnotation =
      DeclarativeHelper.getAnnotationOrThrow(OutboundEndpoint.class, this);

  private final String endpointId =
      StringUtils.defaultIfEmpty(
          outboundEndpointAnnotation.endpointId(),
          formatEndpointId(getEndpointType(), getScenarioId(), getConnectorId()));

  @Override
  public String getEndpointId() {
    return endpointId;
  }

  @Override
  public final String getConnectorId() {
    return outboundEndpointAnnotation.belongsToConnector();
  }

  @Override
  public final String getScenarioId() {
    return outboundEndpointAnnotation.consumesFromScenario();
  }
}
