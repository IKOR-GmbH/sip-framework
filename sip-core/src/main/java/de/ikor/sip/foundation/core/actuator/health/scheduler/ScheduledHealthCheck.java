package de.ikor.sip.foundation.core.actuator.health.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduled execution for connection checks.
 *
 * <p>Switched off by default Default fixed delay (interval): every 15 minutes, or 900000ms Default
 * initial delay: 10 seconds, or 10000ms
 *
 * <p>sip.core.metrics.scheduled-health-check.enabled:false
 * sip.core.metrics.scheduled-health-check.fixed-delay:900000
 * sip.core.metrics.scheduled-health-check.initial-delay:10000
 */
@Service
@Slf4j
@ConditionalOnExpression("${sip.core.metrics.scheduled-health-check.enabled:false}")
public class ScheduledHealthCheck {

  @Autowired private HealthEndpoint healthEndpoint;

  /** Scheduled health check */
  @Scheduled(
      fixedDelayString = "${sip.core.metrics.scheduled-health-check.fixed-delay:900000}",
      initialDelayString = "${sip.core.metrics.scheduled-health-check.initial-delay:10000}")
  public void scheduledExecution() {
    log.info("sip.core.health.applicationstatus_{}", healthEndpoint.health().getStatus());
  }
}
