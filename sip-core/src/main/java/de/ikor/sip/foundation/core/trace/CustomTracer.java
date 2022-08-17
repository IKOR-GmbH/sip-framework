package de.ikor.sip.foundation.core.trace;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
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
public class CustomTracer extends DefaultTracer {

  public static final String TRACING_ID = "tracingId";

  private final SIPTraceConfig sipTraceConfig;

  /**
   * Creates new instance of CustomTracer Enables tracing in CamelContext
   *
   * @param exchangeFormatter {@link SIPExchangeFormatter}
   * @param camelContext {@link CamelContext}
   * @param sipTraceConfig set of {@link SIPTraceConfig}
   */
  public CustomTracer(
      SIPExchangeFormatter exchangeFormatter,
      CamelContext camelContext,
      SIPTraceConfig sipTraceConfig) {
    setExchangeFormatter(exchangeFormatter);
    camelContext.setTracing(true);
    this.sipTraceConfig = sipTraceConfig;
  }

  @Override
  public void traceBeforeRoute(NamedRoute route, Exchange exchange) {
    setTracingID(exchange);
    super.traceBeforeRoute(route, exchange);
  }

  @Override
  public void traceBeforeNode(NamedNode node, Exchange exchange) {
    setTracingID(exchange);
    super.traceBeforeNode(node, exchange);
  }

  private void setTracingID(Exchange exchange) {
    exchange.getIn().setHeader(TRACING_ID, tracingList(exchange));
  }

  private String tracingList(Exchange exchange) {
    String list = exchange.getIn().getHeader(TRACING_ID, String.class);
    if (list == null) {
      list = exchange.getExchangeId();
    }
    if (!list.contains(exchange.getExchangeId())) {
      list = list.concat("," + exchange.getExchangeId());
    }
    return list;
  }

  @Override
  protected void dumpTrace(String out, Object node) {
    if (sipTraceConfig.isLog()) {
      super.dumpTrace(out, node);
    }
  }
}
