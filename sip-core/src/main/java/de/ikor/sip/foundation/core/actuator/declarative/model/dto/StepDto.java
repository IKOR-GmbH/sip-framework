package de.ikor.sip.foundation.core.actuator.declarative.model.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepDto {
  private Boolean requestPreparation;
  private Boolean responseHandling;
  private String consumerId;
  private Boolean conditioned;
  private String predicateDef;
  private Integer stepOrder;
  private List<StepDto> nested;
}
