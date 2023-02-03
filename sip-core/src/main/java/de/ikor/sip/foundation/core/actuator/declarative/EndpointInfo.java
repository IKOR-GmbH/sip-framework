package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.annonations.OutboundEndpoint;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class which represents POJO model for exposing {@link InboundEndpoint} and {@link
 * OutboundEndpoint}
 */
@Data
@NoArgsConstructor
public class EndpointInfo {

  private String id;
  private String camelEndpointUri;
  private String connector;
  private String scenario;
}
