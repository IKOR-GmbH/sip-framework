package de.ikor.sip.foundation.core.trace;

import de.ikor.sip.foundation.core.trace.model.ExchangeTracePoint;
import de.ikor.sip.foundation.core.trace.model.TraceUnit;
import de.ikor.sip.foundation.core.util.SIPExchangeHelper;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.impl.engine.DefaultTracer;
import org.apache.camel.support.MessageHelper;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.URISupport;
import org.springframework.stereotype.Component;

/**
 * Implementation of Apache Camel's {@link DefaultTracer} Requires sip.core.tracing.enabled=true to
 * be registered as component
 */
@Slf4j
@Component
public class CustomTracer extends DefaultTracer {
  public static final String TRACING_ID = "tracingId";

  private final TraceHistory traceHistory;
  private boolean shouldStore;

  /**
   * Creates new instance of CustomTracer Enables tracing in CamelContext
   *
   * @param traceHistory {@link TraceHistory}
   * @param exchangeFormatter {@link SIPExchangeFormatter}
   * @param camelContext {@link CamelContext}
   */
  public CustomTracer(
      TraceHistory traceHistory,
      SIPExchangeFormatter exchangeFormatter,
      CamelContext camelContext,
      SIPTraceConfig sipTraceConfig) {
    setExchangeFormatter(exchangeFormatter);
    camelContext.setTracing(true);
    this.traceHistory = traceHistory;
    this.shouldStore = sipTraceConfig.isStoreInMemory();
  }

  @Override
  public void traceBeforeRoute(NamedRoute route, Exchange exchange) {
    addTracingId(exchange);
    super.traceBeforeRoute(route, exchange);
    if (shouldStore) {
      String label = URISupport.sanitizeUri(route.getEndpointUrl());
      boolean original = route.getRouteId().equals(exchange.getFromRouteId());
      ExchangeTracePoint tracePoint =
          original ? ExchangeTracePoint.START : ExchangeTracePoint.INCOMING;
      storeInMemory(exchange, tracePoint, route.getRouteId(), label);
    }
  }

  @Override
  public void traceAfterRoute(NamedRoute route, Exchange exchange) {
    addTracingId(exchange);
    super.traceAfterRoute(route, exchange);
    if (shouldStore) {
      String label = URISupport.sanitizeUri(route.getEndpointUrl());
      boolean original = route.getRouteId().equals(exchange.getFromRouteId());
      ExchangeTracePoint tracePoint =
          original ? ExchangeTracePoint.DONE : ExchangeTracePoint.RETURNING;
      storeInMemory(exchange, tracePoint, route.getRouteId(), label);
    }
  }

  @Override
  public void traceBeforeNode(NamedNode node, Exchange exchange) {
    addTracingId(exchange);
    super.traceBeforeNode(node, exchange);
    if (shouldStore) {
      String label = URISupport.sanitizeUri(node.getLabel());
      storeInMemory(exchange, ExchangeTracePoint.ONGOING, node.getId(), label);
    }
  }

  private void addTracingId(Exchange exchange) {
    String list = exchange.getIn().getHeader(TRACING_ID, String.class);
    if (list == null) {
      list = exchange.getExchangeId();
    }
    if (!list.contains(exchange.getExchangeId())) {
      list = list.concat("," + exchange.getExchangeId());
    }
    exchange.getIn().setHeader(TRACING_ID, list);
  }

  private void storeInMemory(Exchange out, ExchangeTracePoint tracePoint, String id, String uri) {
    TraceUnit traceUnit = new TraceUnit();
    enrichTraceUnit(traceUnit, out);
    traceUnit.setTracePoint(tracePoint);
    traceUnit.setNodeId(id);
    traceUnit.setUri(uri);
    traceHistory.add(traceUnit);
  }

  public void enrichTraceUnit(TraceUnit traceUnit, Exchange exchange) {
    Message in = exchange.getIn();
    traceUnit.setExchangeId(exchange.getExchangeId());
    traceUnit.setExchangePattern(exchange.getPattern());
    traceUnit.setProperties(exchange.getProperties());
    try {
      traceUnit.setInternalProperties(((ExtendedExchange) exchange).getInternalProperties());
    } catch (Exception e) {
      log.error("Exchange is not an instance of ExtendedExchange");
    }
    traceUnit.setHeaders(SIPExchangeHelper.filterNonSerializableHeaders(exchange));
    traceUnit.setBodyType(ObjectHelper.classCanonicalName(in.getBody()));
    traceUnit.setBody(MessageHelper.extractBodyAsString(in));
    Exception exception = exchange.getException();
    boolean caught = false;
    if (exception == null) {
      exception = exchange.getProperty(ExchangePropertyKey.EXCEPTION_CAUGHT, Exception.class);
      caught = true;
    }
    if (exception != null) {
      if (caught) {
        traceUnit.setCaughtExceptionType(exception.getClass().getCanonicalName());
        traceUnit.setCaughtExceptionMessage(exception.getMessage());
      } else {
        traceUnit.setExceptionType(exception.getClass().getCanonicalName());
        traceUnit.setExceptionMessage(exception.getMessage());
      }
      StringWriter sw = new StringWriter();
      exception.printStackTrace(new PrintWriter(sw));
      traceUnit.setStackTrace(sw.toString());
    }
  }
}
