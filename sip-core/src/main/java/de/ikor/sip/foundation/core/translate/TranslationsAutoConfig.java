package de.ikor.sip.foundation.core.translate;

import de.ikor.sip.foundation.core.annotation.SIPFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.translate package, based
 * on sip.core.translation.enabled value (true by default).
 */
@ComponentScan
@SIPFeature(name = "translation")
@ConditionalOnProperty(value = "sip.core.translation.enabled", havingValue = "true")
public class TranslationsAutoConfig {}
