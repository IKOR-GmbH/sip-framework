package de.ikor.sip.foundation.core.actuator.declarative.model;

import de.ikor.sip.foundation.core.declarative.annonations.Connector;
import java.util.List;
import lombok.*;

/** Class which represents POJO model for exposing {@link Connector} */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectorInfo {

  private String connectorId;
  private String connectorDescription;
  private List<String> inboundEndpoints;
  private List<String> outboundEndpoints;
}
