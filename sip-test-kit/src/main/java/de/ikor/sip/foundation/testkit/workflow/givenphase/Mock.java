package de.ikor.sip.foundation.testkit.workflow.givenphase;

import de.ikor.sip.foundation.testkit.workflow.TestExecutionStatus;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.Exchange;

/** Parent class for mocks. Different implementations should extend it. */
@Getter
@Setter
public abstract class Mock {

  public static final String ENDPOINT_ID_EXCHANGE_PROPERTY = "connectionAlias";

  protected String testName;
  protected Exchange returnExchange;

  /**
   * Sets mocks behavior
   *
   * @param testExecutionStatus that the mock should fill with details of the test run
   */
  public abstract void setBehavior(TestExecutionStatus testExecutionStatus);

  /** Clear previously set behaviour for mock. */
  public abstract void clear();

  /** @return ID of the Mock */
  public String getId() {
    return returnExchange.getProperty(ENDPOINT_ID_EXCHANGE_PROPERTY, String.class);
  }
}
