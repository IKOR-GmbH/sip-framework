package de.ikor.sip.foundation.core.proxies;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.support.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Proxy for Apache Camel Processors */
public class ProcessorProxy implements Processor {
  private static final Logger logger = LoggerFactory.getLogger(ProcessorProxy.class);

  private final NamedNode nodeDefinition;
  private final Processor target;
  private final List<ProxyExtension> extensions;
  private final Map<String, Consumer<Exchange>> proxyCommands;

  private Function<Exchange, Exchange> mockFunction;

  private static final String TRACING_ID = "tracingId";

  /**
   * Creates new instance of ProcessorProxy
   *
   * @param nodeDefinition {@link NamedNode}
   * @param target target {@link Processor}
   * @param extensions List of {@link ProxyExtension}
   */
  public ProcessorProxy(
      NamedNode nodeDefinition,
      Processor target,
      List<ProxyExtension> extensions) {
    this.nodeDefinition = nodeDefinition;
    this.target = target;
    this.extensions = new ArrayList<>(extensions);
    this.mockFunction = null;
    this.proxyCommands = initCommands();
  }

  private Map<String, Consumer<Exchange>> initCommands() {
    Map<String, Consumer<Exchange>> commands = new HashMap<>();
    commands.put("mock", this::mockProcessing);
    commands.put("process", this::processExchange);
    return commands;
  }

  /** Resets the state of the proxy to default. */
  public synchronized void reset() {
    mockFunction = null;
  }

  /**
   * Sets proxy state to mock. In this mode, it will simply return the result of invoking of the
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

  @Override
  public void process(Exchange exchange) {
    if (exchange.getIn().getHeader(TRACING_ID) == null) {
      exchange.getIn().setHeader(TRACING_ID, exchange.getExchangeId());
    }
    Exchange originalExchange = exchange.copy();

    for (String key : getCommands(exchange)) {
      proxyCommands.get(key).accept(exchange);
    }

    for (ProxyExtension extension : extensions) {
      if (extension.isApplicable(originalExchange, exchange)) {
        extension.run(originalExchange, exchange);
      }
    }
  }

  private List<String> getCommands(Exchange exchange) {
    Map<String, List<String>> proxyModes = new HashMap<>();
    ObjectMapper mapper = new ObjectMapper();
    String proxyModesString = exchange.getIn().getHeader("proxy-modes", String.class);
    if (proxyModesString != null) {
      try {
        proxyModes = mapper.readValue(proxyModesString, Map.class);
      } catch (IOException e) {
        throw new IllegalArgumentException("Error occurred when resolving proxy-modes header", e);
      }
    }
    return proxyModes.getOrDefault(getId(), Arrays.asList("process"));
  }

  @SneakyThrows
  private void processExchange(Exchange exchange) {
    logger.trace("Executing routing logic for the {}", exchange);
    target.process(exchange);
  }

  private void mockProcessing(Exchange exchange) {
    if (mockFunction == null) {
      throw new MockMissingFunctionException(
          "Mock function in ProxyProcessor with id '" + getId() + "' is not defined");
    }
    ExchangeHelper.copyResults(exchange, mockFunction.apply(exchange));
  }

  private String getId() {
    return this.nodeDefinition.getId();
  }

  public Function<Exchange, Exchange> getMockFunction() {
    return this.mockFunction;
  }
}
