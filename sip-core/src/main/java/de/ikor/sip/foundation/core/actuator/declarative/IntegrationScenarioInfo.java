package de.ikor.sip.foundation.core.actuator.declarative;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationScenarioInfo {

  String scenarioId;
  String scenarioDescription;
  String requestModelClass;
  String responseModelClass;
}
