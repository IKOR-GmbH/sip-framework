package de.ikor.sip.foundation.core.premiumsupport.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/** This class handles the SIP Backend registration of an adapter instance. */
@Slf4j
@Component
@RequiredArgsConstructor
@ComponentScan()
@ConditionalOnProperty(name = "sip.core.backend-registration.enabled", havingValue = "true")
public class AdapterRegistration implements InitializingBean, DisposableBean {
  private final SIPRegistrationWebClient sipRegistrationWebClient;
  private final SIPRegistrationScheduler sipRegistrationScheduler;


  /** Is executed after the application has been started and its properties have been set. */
  @Override
  public void afterPropertiesSet() {
    log.info("Initial adapter registration for is starting");
    this.sipRegistrationScheduler.startScheduler();
  }

  /** Is executed just before the application terminates. */
  @Override
  public void destroy() {
    log.info("Sending unregister request before shutting down");
    this.sipRegistrationScheduler.stopScheduler();
    this.sipRegistrationWebClient.unregisterAdapter();
  }
}
