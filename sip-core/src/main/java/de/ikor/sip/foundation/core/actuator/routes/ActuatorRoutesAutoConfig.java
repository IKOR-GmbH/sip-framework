package de.ikor.sip.foundation.core.actuator.routes;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.routes package,
 * based on sip.core.actuator.routes.enabled value (true by default).
 */
@ComponentScan
@SIPFeature(name = "adapter_routes")
@ConditionalOnProperty(value = "sip.core.actuator.routes.enabled", havingValue = "true")
public class ActuatorRoutesAutoConfig {}
