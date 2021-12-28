package de.ikor.sip.foundation.core.premiumsupport.registration;

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
@Component
class SIPRegistrationScheduler {

  private final Long fixedRate;
  private final ThreadPoolTaskScheduler taskScheduler;
  private ScheduledFuture<?> scheduledTask;

  @Autowired
  public SIPRegistrationScheduler(RegistrationConfigurationProperties properties) {
    this.fixedRate = 3000L;
    this.taskScheduler = new ThreadPoolTaskScheduler();
    this.taskScheduler.setPoolSize(1);
    this.taskScheduler.setRemoveOnCancelPolicy(true);
    this.taskScheduler.setThreadNamePrefix("clientRegistration");
    this.taskScheduler.initialize();
  }

  /**
   * Start the scheduler with a runnable that is executed with a fixed rate.
   *
   * @param runnable that can be a arbitrary runnable
   */
  public void startScheduler(Runnable runnable) {
    this.scheduledTask =
        this.taskScheduler.schedule(
            runnable, new PeriodicTrigger(this.fixedRate, TimeUnit.MILLISECONDS));
  }

  /** Stop the current scheduler and interrupt any existing process it is executing. */
  public void stopScheduler() {
    this.scheduledTask.cancel(true);
  }
}
