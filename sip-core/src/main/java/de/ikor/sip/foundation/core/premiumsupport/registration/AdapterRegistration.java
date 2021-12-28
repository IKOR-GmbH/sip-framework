package de.ikor.sip.foundation.core.premiumsupport.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/** This class handles the SIP Backend registration of an adapter instance. */
@Slf4j
@Component
@RequiredArgsConstructor
class AdapterRegistration implements InitializingBean, DisposableBean {
  private final SIPRegistrationScheduler SIPRegistrationScheduler;
  private final TelemetryDataCollector telemetryDataCollector;
  private final RegistrationWebClient registrationWebClient;


  /** Is executed after the application has been started and its properties have been set. */
  @Override
  public void afterPropertiesSet() {
    log.info("Initial adapter registration for is starting");
    this.SIPRegistrationScheduler.startScheduler(
        () ->
            this.registrationWebClient.sendPostRequest(this.telemetryDataCollector.collectData()));
  }

  /** Is executed just before the application terminates. */
  @Override
  public void destroy() {
    log.info("Sending unregister request before shutting down");
    this.SIPRegistrationScheduler.stopScheduler();
    this.registrationWebClient.sendDeleteRequest(this.telemetryDataCollector.collectData());
  }
}
