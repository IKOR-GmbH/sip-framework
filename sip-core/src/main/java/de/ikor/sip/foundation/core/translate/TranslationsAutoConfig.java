package de.ikor.sip.foundation.core.translate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@ConditionalOnProperty(value = "sip.core.translation.enabled", havingValue = "true")
public class TranslationsAutoConfig {}