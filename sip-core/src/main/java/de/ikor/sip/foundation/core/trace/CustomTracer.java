package de.ikor.sip.foundation.core.trace;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.engine.DefaultTracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Implementation of Apache Camel's {@link DefaultTracer} Requires sip.core.tracing.enabled=true to
 * be registered as component
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "sip.core.tracing", name = "enabled")
public class CustomTracer extends DefaultTracer {

  private final Set<SIPTraceOperation> sipTraceOperations;

  private final TraceHistory traceHistory;

  /**
   * Creates new instance of CustomTracer Enables tracing in CamelContext
   *
   * @param traceHistory {@link TraceHistory}
   * @param exchangeFormatter {@link SIPExchangeFormatter}
   * @param camelContext {@link CamelContext}
   * @param sipTraceOperations set of {@link SIPTraceOperation}
   */
  public CustomTracer(
      TraceHistory traceHistory,
      SIPExchangeFormatter exchangeFormatter,
      CamelContext camelContext,
      Set<SIPTraceOperation> sipTraceOperations) {
    setExchangeFormatter(exchangeFormatter);
    camelContext.setTracing(true);
    this.traceHistory = traceHistory;
    this.sipTraceOperations = sipTraceOperations;
  }

  @Override
  protected void dumpTrace(String out) {
    sipTraceOperations.forEach(traceType -> traceType.execute(this, out));
  }

  void logTrace(String out) {
    super.dumpTrace(out);
  }

  void storeInMemory(String out) {
    traceHistory.add(out);
  }
}
