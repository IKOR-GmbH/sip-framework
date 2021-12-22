package de.ikor.sip.foundation.core.actuator.health.scheduler;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Setup necessary beans for scheduled health check */
@Configuration
@EnableScheduling
public class ScheduledHealthCheckConfiguration {

  @Bean
  HealthGaugeConfiguration createHealthGaugeConfiguration() {
    return new HealthGaugeConfiguration();
  }

  @Bean
  HealthCheckMetricsConfiguration createHealthCheckMetricsConfiguration(
      MeterRegistry registry,
      HealthEndpoint healthEndpoint,
      HealthGaugeConfiguration healthGaugeConfiguration) {
    return new HealthCheckMetricsConfiguration(registry, healthEndpoint, healthGaugeConfiguration);
  }
}
