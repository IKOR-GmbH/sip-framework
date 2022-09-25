package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.CentralRouter;

public class SpyCentralRouter extends CentralRouter {
  public static boolean isConfigured = false;

  @Override
  public String getUseCase() {
    return "null";
  }

  @Override
  public void configure() throws Exception {
    isConfigured = true;
  }
}
