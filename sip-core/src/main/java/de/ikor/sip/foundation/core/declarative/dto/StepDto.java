package de.ikor.sip.foundation.core.declarative.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StepDto {
    private Boolean requestPreparation;
    private Boolean responseHandling;
    private String consumerId;
    private Boolean conditioned;
    private String predicateDef;
    private List<StepDto> nested;
}
