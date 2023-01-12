package de.ikor.sip.foundation.core.trace;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.NamedNode;
import org.apache.camel.NamedRoute;
import org.apache.camel.impl.engine.DefaultTracer;
import org.springframework.stereotype.Component;

/**
 * Implementation of Apache Camel's {@link DefaultTracer} Requires sip.core.tracing.enabled=true to
 * be registered as component
 */
@Slf4j
@Component
public class CustomTracer extends DefaultTracer implements TraceSupport {

  public static final String TRACE_SET = "traceSet";

  private final SIPTraceConfig sipTraceConfig;

  /**
   * Creates new instance of CustomTracer Enables tracing in CamelContext
   *
   * @param exchangeFormatter {@link SIPExchangeFormatter}
   * @param sipTraceConfig set of {@link SIPTraceConfig}
   */
  public CustomTracer(
      SIPExchangeFormatter exchangeFormatter,
      SIPTraceConfig sipTraceConfig) {
    setExchangeFormatter(exchangeFormatter);
    this.sipTraceConfig = sipTraceConfig;
  }

  @Override
  public void traceBeforeRoute(NamedRoute route, Exchange exchange) {
    addIdToTraceSet(exchange);
    super.traceBeforeRoute(route, exchange);
  }

  @Override
  public boolean isFeatureEnabled() {
    return sipTraceConfig.isLog();
  }

  @Override
  public void traceBeforeNode(NamedNode node, Exchange exchange) {
    addIdToTraceSet(exchange);
    super.traceBeforeNode(node, exchange);
  }

  private void addIdToTraceSet(Exchange exchange) {
    exchange.getIn().setHeader(TRACE_SET, updateTraceSet(exchange));
  }

  private TraceSet updateTraceSet(Exchange exchange) {
    TraceSet traceSet = exchange.getIn().getHeader(TRACE_SET, TraceSet.class);
    if (traceSet == null) traceSet = new TraceSet();
    return traceSet.cloneAndAdd(exchange.getExchangeId());
  }

  @Override
  protected void dumpTrace(String out, Object node) {
    if (sipTraceConfig.isLog()) {
      super.dumpTrace(out, node);
    }
  }
}
