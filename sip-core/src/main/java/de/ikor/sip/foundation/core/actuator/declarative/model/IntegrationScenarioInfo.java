package de.ikor.sip.foundation.core.actuator.declarative.model;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import lombok.*;

/** Class which represents POJO model for exposing {@link IntegrationScenario} */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationScenarioInfo {

  private String scenarioId;

  private String scenarioDescription;

  private String requestModelClass;

  private JsonSchema requestJsonForm;

  private String responseModelClass;

  private JsonSchema responseJsonForm;
}
