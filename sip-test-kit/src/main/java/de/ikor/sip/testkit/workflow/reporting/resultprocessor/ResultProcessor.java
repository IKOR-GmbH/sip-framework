package de.ikor.sip.testkit.workflow.reporting.resultprocessor;

import de.ikor.sip.testkit.workflow.TestExecutionStatus;

/** Provides method to process results */
public interface ResultProcessor {

  /**
   * Processes a {@link TestExecutionStatus}
   *
   * @param report {@link TestExecutionStatus}
   */
  void process(TestExecutionStatus report);
}
