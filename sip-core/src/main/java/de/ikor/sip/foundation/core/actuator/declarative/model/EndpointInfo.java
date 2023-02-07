package de.ikor.sip.foundation.core.actuator.declarative.model;

import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.annonations.OutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.EndpointType;
import lombok.*;

/**
 * Class which represents POJO model for exposing {@link InboundEndpoint} and {@link
 * OutboundEndpoint}
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointInfo {

  private String endpointId;
  private EndpointType endpointType;
  private String camelEndpointUri; // TODO: Implement getting these information
  private String connectorId;
  private String scenarioId;
}
