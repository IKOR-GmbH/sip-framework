package de.ikor.sip.foundation.core.actuator.declarative;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ConnectorInfo {

  String connectorId;
  String connectorDescription;
  List<String> participatesIncoming;
  List<String> participatesOutgoing;
}
