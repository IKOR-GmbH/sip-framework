package de.ikor.sip.foundation.core.actuator.declarative.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RouteDeclarativeStructureInfo {
  private String connectorGroupId;
  private String scenarioId;
  private String connectorId;
}
