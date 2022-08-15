package de.ikor.sip.foundation.core.trace;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configuration class to read from property file */
@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "sip.core.tracing")
public class SIPTraceConfig {
  private int limit = 100;
  private boolean storeInMemory = false;
}
