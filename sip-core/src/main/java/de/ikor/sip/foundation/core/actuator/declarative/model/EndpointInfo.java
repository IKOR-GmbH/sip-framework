package de.ikor.sip.foundation.core.actuator.declarative.model;

import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.annonations.OutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.EndpointType;
import lombok.Builder;
import lombok.Value;

/**
 * Class which represents POJO model for exposing {@link InboundEndpoint} and {@link
 * OutboundEndpoint}
 */
@Value
@Builder
public class EndpointInfo {

  String endpointId;
  EndpointType endpointType;
  String camelEndpointUri; // TODO: Implement getting these information
  String connectorId;
  String scenarioId;
}
