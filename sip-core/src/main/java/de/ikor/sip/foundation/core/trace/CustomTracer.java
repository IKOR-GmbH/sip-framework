package de.ikor.sip.foundation.core.trace;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.engine.DefaultTracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Implementation of Apache Camel's {@link DefaultTracer} Requires sip.core.tracing.enabled=true to
 * be registered as component
 */
@Component
@ConditionalOnProperty(prefix = "sip.core.tracing", name = "enabled")
public class CustomTracer extends DefaultTracer {


  private int traceType;

  private final TraceHistory traceHistory;

  /**
   * Creates new instance of CustomTracer Enables tracing in CamelContext
   *
   * @param traceHistory {@link TraceHistory}
   * @param exchangeFormatter {@link SIPExchangeFormatter}
   * @param camelContext {@link CamelContext}
   * @param traceType int
   */
  public CustomTracer(
      TraceHistory traceHistory,
      SIPExchangeFormatter exchangeFormatter,
      CamelContext camelContext,
      int traceType) {
    setExchangeFormatter(exchangeFormatter);
    camelContext.setTracing(true);
    this.traceHistory = traceHistory;
    this.traceType = traceType;
  }

  @Override
  protected void dumpTrace(String out) {
    if (traceType == 0 || traceType == 1) {
      super.dumpTrace(out);
    }
    if (traceType == 0 || traceType == 2) {
      traceHistory.add(out);
    }

  }
}
