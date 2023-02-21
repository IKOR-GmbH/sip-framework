package de.ikor.sip.foundation.core.actuator.declarative.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeclarativeStructureInfo {
  private List<ConnectorGroupInfo> connectorgroups;
  private List<IntegrationScenarioInfo> scenarios;
}
