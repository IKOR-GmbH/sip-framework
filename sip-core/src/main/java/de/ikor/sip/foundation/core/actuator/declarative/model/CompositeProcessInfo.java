package de.ikor.sip.foundation.core.actuator.declarative.model;

import de.ikor.sip.foundation.core.declarative.orchestration.process.dsl.ProcessOrchestrationDefinition;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents POJO model for exposing {@link
 * de.ikor.sip.foundation.core.declarative.annonation.CompositeProcess}
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

  private ProcessOrchestrationDefinition orchestrationDefinition;
}
