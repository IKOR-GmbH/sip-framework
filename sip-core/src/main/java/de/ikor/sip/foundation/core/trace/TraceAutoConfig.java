package de.ikor.sip.foundation.core.trace;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@ConditionalOnProperty(value = "sip.core.tracing.enabled", havingValue = "true")
public class TraceAutoConfig {}
