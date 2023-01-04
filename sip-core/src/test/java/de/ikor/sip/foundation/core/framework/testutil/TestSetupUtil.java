package de.ikor.sip.foundation.core.framework.testutil;

public class TestSetupUtil {
  private static int counter = 0;

  private TestSetupUtil() {}

  public static String getNextEndpointId() {
    counter += 1;
    return "direct:multicast-" + counter;
  }
}
