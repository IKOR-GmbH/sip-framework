package de.ikor.sip.foundation.core.premiumsupport.registration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * In order to trigger the SIP Backend registration of an adapter instance in an interval the {@link
 * SIPRegistrationScheduler} makes sure the functionality is provided.
 */
@Slf4j
@Component
class SIPRegistrationScheduler {

  private final Long fixedRate;
  private ScheduledFuture<?> scheduledTask;
  private final SIPRegistrationWebClient sipRegistrationWebClient;
  private final ThreadPoolTaskScheduler taskScheduler;

  public SIPRegistrationScheduler(
      SIPRegistrationWebClient sipRegistrationWebClient, ThreadPoolTaskScheduler taskScheduler, SIPRegistrationProperties properties) {
    this.fixedRate = properties.getInterval();
    this.sipRegistrationWebClient = sipRegistrationWebClient;
    this.taskScheduler = taskScheduler;
    this.taskScheduler.setRemoveOnCancelPolicy(true);
    this.taskScheduler.setThreadNamePrefix("clientRegistration");
    this.taskScheduler.initialize();
  }

  /** Start the scheduler with a runnable that is executed with a fixed rate. */
  public void startScheduler() {
    if (scheduledTask == null) {
      this.scheduledTask =
          this.taskScheduler.schedule(
              sipRegistrationWebClient::registerAdapter,
              new PeriodicTrigger(this.fixedRate, TimeUnit.MILLISECONDS));
    } else {
      log.warn("SIPRegistrationScheduler is already started");
    }
  }

  /** Stop the current scheduler and interrupt any existing process it is executing. */
  public void stopScheduler() {
    this.scheduledTask.cancel(true);
  }
}
