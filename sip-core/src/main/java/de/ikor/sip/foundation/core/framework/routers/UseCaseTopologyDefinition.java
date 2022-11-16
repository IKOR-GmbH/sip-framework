package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import java.util.LinkedHashMap;
import lombok.Getter;

public class UseCaseTopologyDefinition {
  @Getter private final LinkedHashMap<OutConnector[], String> allConnectors = new LinkedHashMap<>();

  public void sequencedOutput(OutConnector... outConnectors) {
    allConnectors.put(outConnectors, "seq");
  }

  public void parallelOutput(OutConnector... outConnectors) {
    allConnectors.put(outConnectors, "par");
  }
}
