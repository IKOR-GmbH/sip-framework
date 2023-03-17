package de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import lombok.Data;

@Data
public class ScenarioOrderDSL implements ScenarioOrder {

  String fromConnectorID;
  Boolean fromAll;
  ArrayList<String> orderedConnectors;

  Function<Map<String, Object>, Object> method;

  @Override
  public From from(String connectorID) {
    // Objects.requireNonNull(connectorID, "ConnectorID is required");
    this.fromConnectorID = connectorID;
    return new ScenarioOrderDSLFrom(this);
  }

  @Override
  public From fromAll() {
    this.fromAll = Boolean.TRUE;
    return new ScenarioOrderDSLFrom(this);
  }
}
