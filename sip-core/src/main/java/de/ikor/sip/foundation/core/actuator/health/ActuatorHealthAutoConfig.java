package de.ikor.sip.foundation.core.actuator.health;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.health package,
 * based on sip.core.actuator.routes.health.enabled value (true by default).
 */
@ComponentScan
@SIPFeature(name = "health")
@ConditionalOnProperty(value = "sip.core.actuator.health.enabled", havingValue = "true")
public class ActuatorHealthAutoConfig {}
