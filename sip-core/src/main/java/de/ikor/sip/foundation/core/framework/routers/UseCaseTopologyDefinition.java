package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import lombok.Getter;

import java.util.LinkedHashMap;

public class UseCaseTopologyDefinition {
  @Getter private LinkedHashMap<OutConnector[], String> allConnectors = new LinkedHashMap<>();

  public UseCaseTopologyDefinition sequencedOutput(OutConnector... outConnectors) {
    allConnectors.put(outConnectors, "seq");
    return this;
  }

  public UseCaseTopologyDefinition parallelOutput(OutConnector... outConnectors) {
    allConnectors.put(outConnectors, "par");
    return this;
  }
}