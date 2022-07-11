package de.ikor.sip.foundation.core.actuator.info;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import de.ikor.sip.foundation.core.util.FoundationFeature;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.info package,
 * based on sip.core.actuator.extensions.info.enabled value (true by default).
 */
@ComponentScan
@SIPFeature(type = FoundationFeature.INFO, versions = 1)
@ConditionalOnAvailableEndpoint(endpoint = InfoEndpoint.class)
@ConditionalOnProperty(value = "sip.core.actuator.extensions.info.enabled", havingValue = "true")
public class ActuatorInfoAutoConfig {}
