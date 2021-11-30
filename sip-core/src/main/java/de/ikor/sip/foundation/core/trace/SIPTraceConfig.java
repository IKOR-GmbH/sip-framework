package de.ikor.sip.foundation.core.trace;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to read from property file
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "sip.core.tracing")
public class SIPTraceConfig {

    private int limit;
    private int traceType;
    private boolean enabled;

}
