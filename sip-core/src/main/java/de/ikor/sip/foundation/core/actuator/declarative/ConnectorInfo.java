package de.ikor.sip.foundation.core.actuator.declarative;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ConnectorInfo {

  String connectorId;
  String connectorDescription;
  List<String> participatesIncoming;
  List<String> participatesOutgoing;
}
