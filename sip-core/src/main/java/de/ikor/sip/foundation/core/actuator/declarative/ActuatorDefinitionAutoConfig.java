package de.ikor.sip.foundation.core.actuator.declarative;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.routes package,
 * based on management.endpoints.web.exposure.include containing value "adapter-routes".
 */
@ComponentScan
@ConditionalOnExpression(
    value = "'${management.endpoints.web.exposure.include}'.contains('adapterdefinition')")
public class ActuatorDefinitionAutoConfig {}
