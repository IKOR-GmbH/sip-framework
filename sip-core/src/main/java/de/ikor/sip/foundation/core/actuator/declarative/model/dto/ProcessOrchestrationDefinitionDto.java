package de.ikor.sip.foundation.core.actuator.declarative.model.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/** DTO representing definition of composite process */
@Data
@Builder
public class ProcessOrchestrationDefinitionDto {
  private List<StepDto> steps;
}
