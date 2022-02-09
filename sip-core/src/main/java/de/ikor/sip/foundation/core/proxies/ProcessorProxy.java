package de.ikor.sip.foundation.core.proxies;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
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

  private final NamedNode nodeDefinition;
  private final Processor target;
  private final List<ProxyExtension> extensions;
  private final Map<String, Consumer<Exchange>> proxyCommands;
  private final boolean endpointProcessor;
  private Function<Exchange, Exchange> mockFunction;
  private Function<Exchange, Exchange> skipFunction;

  private static final String TRACING_ID = "tracingId";
  private static final String TEST_MODE_HEADER = "test-mode";

  private static final String COMMAND_MOCK = "mock";
  private static final String COMMAND_PROCESS = "process";
  private static final String COMMAND_SKIP = "skip";

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
      boolean endpointProcessor,
      List<ProxyExtension> extensions) {
    this.nodeDefinition = nodeDefinition;
    this.target = target;
    this.extensions = new ArrayList<>(extensions);
    this.endpointProcessor = endpointProcessor;
    this.mockFunction = null;
    this.skipFunction = null;
    this.proxyCommands = initCommands();
  }

  private Map<String, Consumer<Exchange>> initCommands() {
    Map<String, Consumer<Exchange>> commands = new HashMap<>();
    commands.put(COMMAND_MOCK, this::mockProcessing);
    commands.put(COMMAND_PROCESS, this::processExchange);
    commands.put(COMMAND_SKIP, this::skipProcessing);
    return commands;
  }

  /** Resets the state of the proxy to default. */
  public synchronized void reset() {
    mockFunction = null;
    skipFunction = null;
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
   * Sets proxy a function that proxy should execute when it skips original processor logic.
   *
   * @param exchangeFunction callback function for skip behavior
   */
  public synchronized void skipFunction(UnaryOperator<Exchange> exchangeFunction) {
    this.skipFunction = exchangeFunction;
  }

  /**
   * Add new ProxyExtension for this ProcessorProxy
   *
   * @param proxyExtension {@link ProxyExtension}
   */
  public synchronized void addExtension(ProxyExtension proxyExtension) {
    this.extensions.add(proxyExtension);
  }

  /**
   * @return true if this is processor that outputs to Endpoint
   * */
  public boolean isEndpointProcessor() {
    return this.endpointProcessor;
  }

  @Override
  public boolean process(Exchange exchange, AsyncCallback callback) {
    if (exchange.getIn().getHeader(TRACING_ID) == null) {
      exchange.getIn().setHeader(TRACING_ID, exchange.getExchangeId());
    }
    Exchange originalExchange = exchange.copy();

    if (shouldSkipRegularExecution(exchange)) {
      proxyCommands.get(COMMAND_SKIP).accept(exchange);
    } else {
      for (String key : getCommands(exchange)) {
        proxyCommands.get(key).accept(exchange);
      }
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

  private boolean mockedProcessor(Exchange exchange) {
    return getCommands(exchange).contains(COMMAND_MOCK);
  }

  private boolean shouldSkipRegularExecution(Exchange exchange) {
    return isTestMode(exchange) && this.endpointProcessor && !mockedProcessor(exchange);
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
    return proxyModes.getOrDefault(getId(), Collections.singletonList(COMMAND_PROCESS));
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

  private void skipProcessing(Exchange exchange) {
    logger.trace("Skipping execution for the {}", exchange);
    if (skipFunction != null) {
      skipFunction.apply(exchange);
    }
  }

  private String getId() {
    return this.nodeDefinition.getId();
  }
}
