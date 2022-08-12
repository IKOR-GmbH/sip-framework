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
   * @param endpoint {@link Endpoint}
   * @return {@link Exchange} result of route execution
   */
  Exchange invoke(Exchange exchange, Endpoint endpoint);

  /**
   * Matching Endpoint with proper RouteInvoker
   *
   * @param endpoint {@link Endpoint}
   * @return boolean true when matching
   */
  boolean isApplicable(Endpoint endpoint);

  /**
   * Method which classifies which camel components should be suspended during sip batch tests.
   *
   * @return boolean
   */
  default boolean isSuspendable() {
    return false;
  }
}
