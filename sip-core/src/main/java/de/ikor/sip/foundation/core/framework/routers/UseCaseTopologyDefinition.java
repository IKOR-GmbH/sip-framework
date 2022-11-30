package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import java.util.LinkedHashMap;
import lombok.Getter;

public class UseCaseTopologyDefinition {
  @Getter
  private final LinkedHashMap<OutConnector[], String> allConnectors =
      new LinkedHashMap<>();

  private final LinkedHashMap<String, OutConnector[]> connectorsByBoundType =
          new LinkedHashMap<>();

  public void sequencedOutput(OutConnector... outConnectors) {
    connectorsByBoundType.put("seq", outConnectors);
    allConnectors.put(outConnectors, "seq");
  }

  public void parallelOutput(OutConnector... outConnectors) {
    connectorsByBoundType.put("par", outConnectors);
    allConnectors.put(outConnectors, "par");
  }


  public OutConnector[] getConnectorsBindInParallel () {
    return connectorsByBoundType.get("par");
  }

  public OutConnector[] getConnectorsBindInSequence () {
    return connectorsByBoundType.get("seq");
  }
}
