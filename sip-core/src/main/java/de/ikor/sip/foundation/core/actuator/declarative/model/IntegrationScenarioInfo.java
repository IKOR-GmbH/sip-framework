package de.ikor.sip.foundation.core.actuator.declarative.model;

import de.ikor.sip.foundation.core.declarative.annonations.IntegrationScenario;
import lombok.*;

/** Class which represents POJO model for exposing {@link IntegrationScenario} */
@Value
@Builder
public class IntegrationScenarioInfo {

  String scenarioId;
  String scenarioDescription;
  String requestModelClass;
  String responseModelClass;
}
