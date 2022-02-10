package de.ikor.sip.foundation.core.proxies;

import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.SneakyThrows;
import org.apache.camel.*;
import org.apache.camel.support.AsyncProcessorSupport;
import org.apache.camel.support.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Proxy for Apache Camel Processors */
public class ProcessorProxy extends AsyncProcessorSupport {
  private static final Logger logger = LoggerFactory.getLogger(ProcessorProxy.class);
  public static final String TEST_MODE_HEADER = "test-mode";

  private final NamedNode nodeDefinition;
  private final Processor wrappedProcessor;
  private final List<ProxyExtension> extensions;
  private final boolean endpointProcessor;
  private Function<Exchange, Exchange> mockFunction;

  private static final String TRACING_ID = "tracingId";

  /**
   * Creates new instance of ProcessorProxy
   *
   * @param nodeDefinition {@link NamedNode}
   * @param wrappedProcessor target {@link Processor}
   * @param extensions List of {@link ProxyExtension}
   */
  public ProcessorProxy(
      NamedNode nodeDefinition,
      Processor wrappedProcessor,
      boolean endpointProcessor,
      List<ProxyExtension> extensions) {
    this.nodeDefinition = nodeDefinition;
    this.wrappedProcessor = wrappedProcessor;
    this.extensions = new ArrayList<>(extensions);
    this.endpointProcessor = endpointProcessor;
    this.mockFunction = defaultMockFunction();
  }

  /** Resets the state of the proxy to default. */
  public synchronized void reset() {
    mockFunction = null;
  }

  /**
   * Sets proxy's mock function. In this mode, it will simply return the result of invoking of the
   * exchangeFunction.
   *
   * @param exchangeFunction callback function for mock behavior
   */
  public synchronized void mock(UnaryOperator<Exchange> exchangeFunction) {
    this.mockFunction = exchangeFunction;
  }

  /**
   * Add new ProxyExtension for this ProcessorProxy
   *
   * @param proxyExtension {@link ProxyExtension}
   */
  public synchronized void addExtension(ProxyExtension proxyExtension) {
    this.extensions.add(proxyExtension);
  }

  /** @return true if this is a processor that outputs to Endpoint */
  public boolean isEndpointProcessor() {
    return this.endpointProcessor;
  }

  @Override
  public boolean process(Exchange exchange, AsyncCallback callback) {
    if (exchange.getIn().getHeader(TRACING_ID) == null) {
      exchange.getIn().setHeader(TRACING_ID, exchange.getExchangeId());
    }
    Exchange originalExchange = exchange.copy();

    if (isTestMode(exchange) && hasMockFunction()) {
      mockProcessing(exchange);
    } else {
      processExchange(exchange);
    }

    for (ProxyExtension extension : extensions) {
      if (extension.isApplicable(originalExchange, exchange)) {
        extension.run(originalExchange, exchange);
      }
    }

    callback.done(true);
    return true;
  }

  private boolean isTestMode(Exchange exchange) {
    return "true".equals(exchange.getIn().getHeader(TEST_MODE_HEADER, String.class));
  }

  @SneakyThrows
  private void processExchange(Exchange exchange) {
    logger.trace("Processor: {}, Executing routing logic for the {}", getId(), exchange);
    wrappedProcessor.process(exchange);
  }

  private boolean hasMockFunction() {
    return this.mockFunction != null;
  }

  private void mockProcessing(Exchange exchange) {
    logger.trace("Processor: {}, Executing mocking logic for the {} ", getId(), exchange);
    ExchangeHelper.copyResults(exchange, mockFunction.apply(exchange));
  }

  private String getId() {
    return this.nodeDefinition.getId();
  }

  private Function<Exchange, Exchange> defaultMockFunction() {
    if (isEndpointProcessor()) {
      // do nothing
      return exchange -> exchange;
    }
    return null;
  }
}
