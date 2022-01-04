package de.ikor.sip.foundation.core.premiumsupport.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Component;

/**
 * This class handles the SIP Backend registration of an adapter instance. It also scans whole
 * premiumsupport.registration package, and depends on sip.core.backend-registration.enabled
 * configuration.
 */
@Slf4j
@Component
@ComponentScan()
@RequiredArgsConstructor
@ConditionalOnProperty(name = "sip.core.backend-registration.enabled", havingValue = "true")
public class AdapterRegistration implements InitializingBean, DisposableBean {
  private final ScheduledAnnotationBeanPostProcessor processor;
  private final SIPRegistrationWebClient sipRegistrationWebClient;

  /**
   * Is executed after the application has been started and its properties have been set, and
   * scheduled for further invocations based on sip.core.backend-registration.interval property.
   */
  @Override
  @Scheduled(fixedDelayString = "${sip.core.backend-registration.interval}")
  public void afterPropertiesSet() {
    this.sipRegistrationWebClient.registerAdapter();
  }

  /**
   * Is executed just before the application terminates. It cancels all scheduled tasks before
   * unregistering adapter on the platform.
   */
  @Override
  public void destroy() {
    log.info("Canceling all annotation-scheduled tasks.");
    this.processor.getScheduledTasks().forEach(ScheduledTask::cancel);

    log.info("Sending unregister request before shutting down");
    this.sipRegistrationWebClient.unregisterAdapter();
  }
}
