package de.ikor.sip.foundation.core.actuator.declarative.model;

import de.ikor.sip.foundation.core.declarative.dto.ProcessOrchestrationDefinitionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * POJO model for exposing {@link
 * de.ikor.sip.foundation.core.declarative.annonation.CompositeProcess} on the actuator endpoint
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompositeProcessInfo {

  private String processId;

  private String providerId;

  private List<String> consumerIds;

  private String processDescription;

  private ProcessOrchestrationDefinitionDto orchestrationDefinition;
}
