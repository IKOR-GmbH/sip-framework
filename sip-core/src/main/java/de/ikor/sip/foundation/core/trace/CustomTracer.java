package de.ikor.sip.foundation.core.trace;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.engine.DefaultTracer;
import org.springframework.stereotype.Component;

/**
 * Implementation of Apache Camel's {@link DefaultTracer} Requires sip.core.tracing.enabled=true to
 * be registered as component
 */
@Component
public class CustomTracer extends DefaultTracer {

  private SIPTraceTypeEnum traceType;

  private final TraceHistory traceHistory;

  /**
   * Creates new instance of CustomTracer Enables tracing in CamelContext
   *
   * @param traceHistory {@link TraceHistory}
   * @param exchangeFormatter {@link SIPExchangeFormatter}
   * @param camelContext {@link CamelContext}
   * @param traceConfig {@link SIPTraceConfig}
   */
  public CustomTracer(
      TraceHistory traceHistory,
      SIPExchangeFormatter exchangeFormatter,
      CamelContext camelContext,
      SIPTraceConfig traceConfig) {
    setExchangeFormatter(exchangeFormatter);
    camelContext.setTracing(true);
    this.traceHistory = traceHistory;
    this.traceType = traceConfig.getTraceType();
  }

  @Override
  protected void dumpTrace(String out) {
    if (traceType == SIPTraceTypeEnum.BOTH || traceType == SIPTraceTypeEnum.LOG) {
      super.dumpTrace(out);
    }
    if (traceType == SIPTraceTypeEnum.BOTH || traceType == SIPTraceTypeEnum.MEMORY) {
      traceHistory.add(out);
    }
  }
}
