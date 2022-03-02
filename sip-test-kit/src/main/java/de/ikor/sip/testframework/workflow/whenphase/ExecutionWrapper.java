package de.ikor.sip.testframework.workflow.whenphase;

import de.ikor.sip.testframework.workflow.whenphase.executor.Executor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.camel.Exchange;

/** Executes WhenPhaseDefinition */
@Data
@AllArgsConstructor
public class ExecutionWrapper {

  private String testName;
  private Executor executor;
  private Exchange whenPhaseDefinition;

  /**
   * WhenPhaseDefinition
   *
   * @return {@link Exchange}
   */
  public Exchange execute() {
    return executor.execute(whenPhaseDefinition, testName);
  }
}
