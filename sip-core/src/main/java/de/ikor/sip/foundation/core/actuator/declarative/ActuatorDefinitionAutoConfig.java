package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import de.ikor.sip.foundation.core.util.FoundationFeature;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.actuator.declarative
 * package..
 */
@SIPFeature(name = FoundationFeature.ADAPTER_DEFINITION, versions = 1)
@ComponentScan
@AutoConfiguration
@ConditionalOnExpression(
    "#{${sip.core.actuator.adapterdefinition.enabled:true} and ${sip.core.declarativestructure.enabled:true}}")
public class ActuatorDefinitionAutoConfig {}
