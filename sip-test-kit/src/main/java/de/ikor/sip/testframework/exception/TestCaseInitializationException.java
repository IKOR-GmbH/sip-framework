package de.ikor.sip.testframework.exception;

import de.ikor.sip.testframework.workflow.TestCase;
import lombok.extern.slf4j.Slf4j;

/** General exception for {@link TestCase} initialization */
@Slf4j
public class TestCaseInitializationException extends RuntimeException {

  /**
   * Defines exception message and type of exception
   *
   * @param message exception message
   * @param exceptionType type of exception {@link ExceptionType}
   */
  public TestCaseInitializationException(String message, ExceptionType exceptionType) {
    super("Error occurred while initializing " + exceptionType + ", message received: " + message);
  }
}
