package de.ikor.sip.foundation.core.actuator.declarative;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorInfo {

  String connectorId;
  String connectorDescription;
  List<String> inboundEndpoints = new ArrayList<>();
  List<String> outboundEndpoints = new ArrayList<>();
}
