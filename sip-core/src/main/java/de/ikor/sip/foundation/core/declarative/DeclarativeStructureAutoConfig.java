package de.ikor.sip.foundation.core.declarative;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@ConditionalOnProperty(value = "sip.core.declarativestructure.enabled", havingValue = "true")
public class DeclarativeStructureAutoConfig {}
