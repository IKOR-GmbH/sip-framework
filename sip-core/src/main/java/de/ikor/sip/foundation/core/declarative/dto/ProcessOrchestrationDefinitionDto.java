package de.ikor.sip.foundation.core.declarative.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessOrchestrationDefinitionDto {
  private List<StepDto> steps;
}
