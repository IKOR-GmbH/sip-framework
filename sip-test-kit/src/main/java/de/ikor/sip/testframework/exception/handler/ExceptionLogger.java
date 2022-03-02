package de.ikor.sip.testframework.exception.handler;

import lombok.extern.slf4j.Slf4j;

/** Provides static methods for logging exceptions */
@Slf4j
public class ExceptionLogger {

  private ExceptionLogger() {}

  /**
   * Logs generic exceptions
   *
   * @param ex exception thrown
   */
  public static void logException(Throwable ex) {
    log.error("Exception: {}", ex.getClass().toString());
    log.error("Message: {}", ex.getMessage());
  }

  /**
   * Logs exception for specific TestCase
   *
   * @param e exception thrown
   * @param testCaseName name of TestCase
   */
  public static void logTestCaseException(Exception e, String testCaseName) {
    log.error("Error occurred during initialization of test case: {}", testCaseName);
    log.error("Exception: {}", e.getClass().toString());
    log.error("Message: {}", e.getMessage());
  }
}
