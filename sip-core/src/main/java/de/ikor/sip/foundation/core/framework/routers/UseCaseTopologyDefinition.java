package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;

import java.util.LinkedHashMap;
import java.util.Optional;

public class UseCaseTopologyDefinition {
  private final LinkedHashMap<String, OutConnector[]> connectorsByBoundType =
          new LinkedHashMap<>();

  public void sequencedOutput(OutConnector... outConnectors) {
    connectorsByBoundType.put("seq", outConnectors);
  }

  public void parallelOutput(OutConnector... outConnectors) {
    connectorsByBoundType.put("par", outConnectors);
  }

  public Optional<OutConnector[]> getConnectorsBindInParallel () {
    return Optional.ofNullable(connectorsByBoundType.get("par"));
  }

  public Optional<OutConnector[]> getConnectorsBindInSequence () {
    return Optional.ofNullable(connectorsByBoundType.get("seq"));
  }
}
