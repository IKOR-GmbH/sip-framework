package de.ikor.sip.foundation.core.registration;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.Assert;

/**
 * In order to trigger the SIP Backend registration of an adapter instance in an interval the {@link
 * RegistrationScheduler} makes sure the functionality is provided.
 */
class RegistrationScheduler {

  private final ThreadPoolTaskScheduler taskScheduler;
  private final Long fixedRate;
  private volatile ScheduledFuture<?> scheduledTask;

  public RegistrationScheduler(Long fixedRate) {
    Assert.isTrue(
        fixedRate >= 1000 && fixedRate <= 120000,
        "The value of register interval has to between 1000ms and 120000ms.");
    this.fixedRate = fixedRate;
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
