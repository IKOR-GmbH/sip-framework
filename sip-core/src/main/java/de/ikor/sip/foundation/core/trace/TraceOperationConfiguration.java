package de.ikor.sip.foundation.core.trace;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures bean "sipTraceOperations" with trace types defined in "trace-type" property or all if
 * * is set
 */
@Configuration
public class TraceOperationConfiguration {

  @Bean
  public Set<SIPTraceOperation> sipTraceOperations(SIPTraceConfig traceConfig) {
    LinkedHashSet<SIPTraceOperation> set = new LinkedHashSet<>();
    if (traceConfig.getTraceType().contains("*")) {
      Arrays.stream(SIPTraceOperation.values()).forEach(set::add);
    } else {
      traceConfig.getTraceType().forEach(name -> set.add(SIPTraceOperation.valueOf(name)));
    }
    return set;
  }
}
