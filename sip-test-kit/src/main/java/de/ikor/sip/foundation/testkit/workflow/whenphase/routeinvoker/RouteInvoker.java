package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;

/** Route invoker interface */
public interface RouteInvoker {

  String TEST_NAME_HEADER = "test-name";

  /**
   * Sends request to route
   *
   * @param exchange {@link Exchange}
   * @return {@link Exchange} result of route execution
   */
  Exchange invoke(Exchange exchange);

  /**
   * Matching Endpoint with proper RouteInvoker
   *
   * @param endpoint {@link Endpoint}
   * @return boolean true when matching
   */
  boolean isApplicable(Endpoint endpoint);

  /**
   * Set endpoint to proper RouteInvoker
   *
   * @param endpoint {@link Endpoint}
   * @return {@link RouteInvoker} result of route execution
   */
  RouteInvoker setEndpoint(Endpoint endpoint);


  /**
   * Method which classifies which camel components should
   * be suspended during sip batch tests.
   *
   * @return boolean
   */
  default boolean isSuspendable() {
    return false;
  }
}
