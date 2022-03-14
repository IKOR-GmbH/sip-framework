package de.ikor.sip.foundation.testkit.workflow.whenphase.executor;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/** Request executor interface */
@Component
public interface Executor {

  String TEST_NAME_HEADER = "test-name";
  /**
   * Starts request execution
   *
   * @param exchange {@link Exchange}
   * @return {@link Exchange} result of request execution
   */
  Exchange execute(Exchange exchange, String testName);
}
