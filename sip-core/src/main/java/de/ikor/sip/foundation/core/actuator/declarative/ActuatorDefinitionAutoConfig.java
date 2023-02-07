package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import de.ikor.sip.foundation.core.util.FoundationFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.declarative
 * package..
 */
@ComponentScan
@SIPFeature(name = FoundationFeature.ADAPTER_DEFINITION, versions = 1)
@ConditionalOnProperty(value = "sip.core.actuator.adapterdefinition.enabled", havingValue = "true")
public class ActuatorDefinitionAutoConfig {}
