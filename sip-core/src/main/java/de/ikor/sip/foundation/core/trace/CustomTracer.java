package de.ikor.sip.foundation.core.trace;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.engine.DefaultTracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Implementation of Apache Camel's {@link DefaultTracer} Requires sip.core.tracing.enabled=true to
 * be registered as component
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "sip.core.tracing", name = "enabled")
public class CustomTracer extends DefaultTracer {

  private final Set<SIPTraceTypeEnum> sipTraceTypeEnums;

  private final TraceHistory traceHistory;

  /**
   * Creates new instance of CustomTracer Enables tracing in CamelContext
   *
   * @param traceHistory {@link TraceHistory}
   * @param exchangeFormatter {@link SIPExchangeFormatter}
   * @param camelContext {@link CamelContext}
   * @param sipTraceTypeEnums set of {@link SIPTraceTypeEnum}
   */
  public CustomTracer(
      TraceHistory traceHistory,
      SIPExchangeFormatter exchangeFormatter,
      CamelContext camelContext,
      Set<SIPTraceTypeEnum> sipTraceTypeEnums) {
    setExchangeFormatter(exchangeFormatter);
    camelContext.setTracing(true);
    this.traceHistory = traceHistory;
    this.sipTraceTypeEnums = sipTraceTypeEnums;
  }

  @Override
  protected void dumpTrace(String out) {
    sipTraceTypeEnums.forEach(traceType -> traceType.execute(this, out));
  }

  void logTrace(String out) {
    super.dumpTrace(out);
  }

  void storeInMemory(String out) {
    traceHistory.add(out);
  }
}
