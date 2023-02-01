package de.ikor.sip.foundation.testkit.exception;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.testkit.config.CamelContextLifecycleHandler;

/** Exception for unsuspended routes used in {@link CamelContextLifecycleHandler} */
public class UnsuspendedRouteException extends SIPFrameworkException {

  public UnsuspendedRouteException(String routeId) {
    super(
        String.format(
            "Route with route id %s can't be suspended. Shutting down the system.", routeId));
  }

  /**
   * Method for hiding stack trace in console
   *
   * @return Throwable
   */
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
