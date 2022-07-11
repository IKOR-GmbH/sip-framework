package de.ikor.sip.foundation.core.actuator.declarative;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IntegrationScenarioInfo {

  String scenarioId;
  String scenarioDescription;
  String domainModelClass;
}
