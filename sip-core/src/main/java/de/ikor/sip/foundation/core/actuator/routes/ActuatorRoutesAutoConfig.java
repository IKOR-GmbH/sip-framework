package de.ikor.sip.foundation.core.actuator.routes;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import de.ikor.sip.foundation.core.util.FoundationFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.routes package.
 */
@ComponentScan
@SIPFeature(name = FoundationFeature.ADAPTER_ROUTES, versions = 1)
@ConditionalOnProperty(value = "sip.core.actuator.adapter-routes.enabled", havingValue = "true")
public class ActuatorRoutesAutoConfig {}
