package de.ikor.sip.foundation.core.actuator.declarative;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.declarative
 * package..
 */
@ComponentScan
@ConditionalOnProperty(value = "sip.core.actuator.adapterdefinition.enabled", havingValue = "true")
public class ActuatorDefinitionAutoConfig {}
