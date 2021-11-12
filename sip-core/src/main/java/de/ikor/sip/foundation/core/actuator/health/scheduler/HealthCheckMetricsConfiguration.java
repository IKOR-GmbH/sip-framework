package de.ikor.sip.foundation.core.actuator.health.scheduler;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to export overall status from HealthStatus with micrometer to provide metrics on
 * this
 */
@Configuration
public class HealthCheckMetricsConfiguration {

  /**
   * Creates new instance of HealthCheckMetricsConfiguration
   *
   * @param registry {@link MeterRegistry}
   * @param healthEndpoint {@link HealthEndpoint}
   * @param healthGaugeConfiguration {@link HealthGaugeConfiguration}
   */
  public HealthCheckMetricsConfiguration(
      MeterRegistry registry,
      HealthEndpoint healthEndpoint,
      HealthGaugeConfiguration healthGaugeConfiguration) {
    Gauge.builder(healthGaugeConfiguration.getName(), healthEndpoint, this::getStatusCode)
        .strongReference(true)
        .register(registry);
  }

  private int getStatusCode(HealthEndpoint healthEndpoint) {
    return healthEndpoint.health().getStatus().equals(Status.UP) ? 0 : 1;
  }
}
