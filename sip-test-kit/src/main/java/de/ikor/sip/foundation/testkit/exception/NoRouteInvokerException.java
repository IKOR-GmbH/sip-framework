package de.ikor.sip.foundation.testkit.exception;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvokerFactory;

/** Exception for non-existent {@link RouteInvoker} in {@link RouteInvokerFactory} */
public class NoRouteInvokerException extends Exception {

  public NoRouteInvokerException(String routeId) {
    super(String.format("No Route Invoker could be found for route id: %s", routeId));
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
