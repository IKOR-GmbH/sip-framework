package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import java.util.LinkedHashMap;
import lombok.Getter;

public class UseCaseTopologyDefinition {
  @Getter
  private final LinkedHashMap<OutConnectorDefinition[], String> allConnectors =
      new LinkedHashMap<>();

  public void sequencedOutput(OutConnectorDefinition... outConnectors) {
    allConnectors.put(outConnectors, "seq");
  }

  public void parallelOutput(OutConnectorDefinition... outConnectors) {
    allConnectors.put(outConnectors, "par");
  }
}
