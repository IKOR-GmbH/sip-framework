package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import java.util.LinkedHashMap;
import lombok.Getter;

public class UseCaseTopologyDefinition {
  @Getter
  private final LinkedHashMap<OutConnectorDefinition[], String> allConnectors =
      new LinkedHashMap<>();

  private final LinkedHashMap<String, OutConnectorDefinition[]> connectorsByBoundType =
          new LinkedHashMap<>();

  public void sequencedOutput(OutConnectorDefinition... outConnectors) {
    connectorsByBoundType.put("seq", outConnectors);
    allConnectors.put(outConnectors, "seq");
  }

  public void parallelOutput(OutConnectorDefinition... outConnectors) {
    connectorsByBoundType.put("par", outConnectors);
    allConnectors.put(outConnectors, "par");
  }


  public OutConnectorDefinition[] getConnectorsBindInParallel () {
    return connectorsByBoundType.get("par");
  }

  public OutConnectorDefinition[] getConnectorsBindInSequence () {
    return connectorsByBoundType.get("seq");
  }
}
