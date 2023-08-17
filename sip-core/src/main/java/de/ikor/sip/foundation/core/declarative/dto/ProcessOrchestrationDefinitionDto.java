package de.ikor.sip.foundation.core.declarative.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProcessOrchestrationDefinitionDto {
    private List<StepDto> steps;
}
