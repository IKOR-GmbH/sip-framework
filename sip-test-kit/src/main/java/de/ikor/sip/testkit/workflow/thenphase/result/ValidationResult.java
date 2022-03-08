package de.ikor.sip.testkit.workflow.thenphase.result;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Class containing results of Validation */
@Data
@AllArgsConstructor
public class ValidationResult {
  private boolean success;
  private String message;
}
