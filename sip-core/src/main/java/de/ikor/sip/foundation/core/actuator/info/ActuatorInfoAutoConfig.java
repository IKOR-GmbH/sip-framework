package de.ikor.sip.foundation.core.actuator.info;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.info package,
 * based on sip.core.actuator.routes.info.enabled value (true by default).
 */
@ComponentScan
@ConditionalOnProperty(value = "sip.core.actuator.info.enabled", havingValue = "true")
public class ActuatorInfoAutoConfig {}
