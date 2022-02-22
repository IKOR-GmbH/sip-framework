package de.ikor.sip.foundation.core.trace;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

/**
 * Class in charge of toggling all beans under de.ikor.sip.foundation.core.trace package, based on
 * sip.core.tracing.enabled value (false by default).
 */
@ComponentScan
@ConditionalOnProperty(value = "sip.core.tracing.enabled", havingValue = "true")
public class TraceAutoConfig {}
