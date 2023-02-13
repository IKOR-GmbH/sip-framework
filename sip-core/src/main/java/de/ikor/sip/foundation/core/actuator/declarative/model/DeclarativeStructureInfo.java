package de.ikor.sip.foundation.core.actuator.declarative.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeclarativeStructureInfo {
  private List<ConnectorGroupInfo> connectorgroups;
  private List<IntegrationScenarioInfo> scenarios;
  private List<ConnectorInfo> connectors;
}
