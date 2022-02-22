package de.ikor.sip.foundation.core.actuator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@ConditionalOnProperty(value = "sip.core.actuator.enabled", havingValue = "true")
public class ActuatorAutoConfig {}
