package de.ikor.sip.testframework.workflow.reporting.resultprocessor;

import de.ikor.sip.testframework.workflow.TestExecutionStatus;

/** Provides method to process results */
public interface ResultProcessor {

  /**
   * Processes a {@link TestExecutionStatus}
   *
   * @param report {@link TestExecutionStatus}
   */
  void process(TestExecutionStatus report);
}
