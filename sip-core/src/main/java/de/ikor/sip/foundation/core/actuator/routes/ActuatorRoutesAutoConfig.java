package de.ikor.sip.foundation.core.actuator.routes;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import de.ikor.sip.foundation.core.util.FoundationFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.routes package,
 * based on management.endpoints.web.exposure.include containing value "adapter-routes".
 */
@ComponentScan
@SIPFeature(name = FoundationFeature.ADAPTER_ROUTES, versions = 1)
@ConditionalOnExpression(
    value = "'${management.endpoints.web.exposure.include}'.contains('adapter-routes')")
public class ActuatorRoutesAutoConfig {}
