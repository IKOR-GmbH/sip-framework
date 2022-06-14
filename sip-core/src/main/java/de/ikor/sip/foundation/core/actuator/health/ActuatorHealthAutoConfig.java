package de.ikor.sip.foundation.core.actuator.health;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@ConditionalOnProperty(value = "sip.core.actuator.health.enabled", havingValue = "true")
public class ActuatorHealthAutoConfig {
}
