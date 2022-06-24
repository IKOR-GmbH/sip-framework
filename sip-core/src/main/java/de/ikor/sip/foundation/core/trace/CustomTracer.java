package de.ikor.sip.foundation.core.trace;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.engine.DefaultTracer;
import org.springframework.stereotype.Component;

/**
 * Implementation of Apache Camel's {@link DefaultTracer} Requires sip.core.tracing.enabled=true to
 * be registered as component
 */
@Slf4j
@Component
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
  protected void dumpTrace(String out, Object node) {
    sipTraceOperations.forEach(traceOperation -> traceOperation.execute(this, out, node));
  }

  void logTrace(String out, Object node) {
    super.dumpTrace(out, node);
  }

  void storeInMemory(String out, Object node) {
    traceHistory.add(out);
  }
}
