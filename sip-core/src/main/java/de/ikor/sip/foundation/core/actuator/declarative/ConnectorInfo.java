package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.declarative.annonations.Connector;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

/** Class which represents POJO model for exposing {@link Connector} */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorInfo {

  String connectorId;
  String connectorDescription;
  List<String> inboundEndpoints = new ArrayList<>();
  List<String> outboundEndpoints = new ArrayList<>();
}
