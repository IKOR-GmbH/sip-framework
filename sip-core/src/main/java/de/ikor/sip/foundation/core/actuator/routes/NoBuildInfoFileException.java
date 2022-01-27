package de.ikor.sip.foundation.core.actuator.routes;

/**
 * Exception when missing build-info spring boot maven plugin and generated build-info.properties
 * file.
 */
public class NoBuildInfoFileException extends RuntimeException {

  /**
   * Creates new instance of NoBuildInfoFileException
   *
   * @param message message to be shown
   */
  public NoBuildInfoFileException(String message) {
    super(message);
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
