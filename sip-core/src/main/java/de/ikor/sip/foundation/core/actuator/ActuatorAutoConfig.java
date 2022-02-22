package de.ikor.sip.foundation.core.actuator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator package, based
 * on sip.core.actuator.enabled value (true by default).
 */
@ComponentScan
@ConditionalOnProperty(value = "sip.core.actuator.enabled", havingValue = "true")
public class ActuatorAutoConfig {}
