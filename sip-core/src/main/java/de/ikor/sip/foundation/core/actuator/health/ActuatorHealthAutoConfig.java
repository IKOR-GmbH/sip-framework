package de.ikor.sip.foundation.core.actuator.health;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import de.ikor.sip.foundation.core.util.FoundationFeature;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.health package,
 * based on sip.core.actuator.extensions.health.enabled value (true by default).
 */
@ComponentScan
@SIPFeature(type = FoundationFeature.HEALTH, versions = 1)
@ConditionalOnAvailableEndpoint(endpoint = HealthEndpoint.class)
@ConditionalOnProperty(value = "sip.core.actuator.extensions.health.enabled", havingValue = "true")
public class ActuatorHealthAutoConfig {}
