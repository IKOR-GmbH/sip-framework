package de.ikor.sip.foundation.core.actuator.health.scheduler;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configuration for configurable health gauge name */
@Data
@Configuration
@ConfigurationProperties(value = "sip.core.metrics.gauge")
public class HealthGaugeConfiguration {
  private String name = "sip.core.metrics.health";
}
