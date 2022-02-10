package de.ikor.sip.foundation.core.proxies;

import static de.ikor.sip.foundation.core.proxies.ProcessorProxy.TEST_MODE_HEADER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import java.util.List;
import java.util.function.UnaryOperator;
import org.apache.camel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessorProxyTest {

  ProcessorProxy processorProxySubject;
  List<ProxyExtension> proxyExtensions;
  NamedNode namedNode;
  Processor processor;
  ProxyExtension proxyExtension;
  AsyncCallback callback;
  Exchange exchange;

  private static final String PROXY_ID = "proxy";

  @BeforeEach
  void setup() {
    namedNode = mock(NamedNode.class);
    processor = mock(Processor.class);
    proxyExtension = mock(ProxyExtension.class);
    proxyExtensions = List.of(proxyExtension);
    callback = mock(AsyncCallback.class);
    exchange = mock(ExtendedExchange.class, RETURNS_DEEP_STUBS);
    processorProxySubject = new ProcessorProxy(namedNode, processor, false, proxyExtensions);
  }

  @Test
  void process_executeMock() throws Exception {
    // arrange
    when(exchange.getPattern()).thenReturn(ExchangePattern.InOut);
    when(exchange.getIn().getHeader(TEST_MODE_HEADER, String.class)).thenReturn("true");

    UnaryOperator<Exchange> mockFunction = mock(UnaryOperator.class);
    when(mockFunction.apply(exchange)).thenReturn(exchange);

    // act
    processorProxySubject.mock(mockFunction);

    assertThatCode(() -> processorProxySubject.process(exchange, callback))
        .doesNotThrowAnyException();

    // assert
    verify(mockFunction, times(1)).apply(exchange);
    verify(processor, times(0)).process(exchange);
  }

  @Test
  void process_executeProcessExchange() throws Exception {
    // arrange
    when(namedNode.getId()).thenReturn(PROXY_ID);
    when(proxyExtension.isApplicable(any(), any())).thenReturn(false);

    // act
    assertThatCode(() -> processorProxySubject.process(exchange, callback))
        .doesNotThrowAnyException();

    // assert
    verify(proxyExtension, times(1)).isApplicable(any(), any());
    verify(processor, times(1)).process(exchange);
  }

  @Test
  void process_addTracingId() {
    // arrange
    when(exchange.getIn().getHeader("tracingId", String.class)).thenReturn("id");

    // act
    assertThatCode(() -> processorProxySubject.process(exchange, callback))
        .doesNotThrowAnyException();
    // assert
    verify(exchange.getIn(), times(1)).setHeader("tracingId", exchange.getExchangeId());
  }

  @Test
  void process_executeProcessExtension() throws Exception {
    // arrange
    when(namedNode.getId()).thenReturn(PROXY_ID);
    when(proxyExtension.isApplicable(any(), any())).thenReturn(true);

    // act
    assertThatCode(() -> processorProxySubject.process(exchange, callback))
        .doesNotThrowAnyException();

    // assert
    verify(processor, times(1)).process(exchange);
    verify(proxyExtension, times(1)).isApplicable(any(), any());
    verify(proxyExtension, times(1)).run(any(), any());
  }

  @Test
  void process_endpointProcessorTestMode() throws Exception {
    // arrange
    ProcessorProxy endpointProcessorProxySubject =
        new ProcessorProxy(namedNode, processor, true, proxyExtensions);
    when(exchange.getIn().getHeader(TEST_MODE_HEADER, String.class)).thenReturn("true");
    when(exchange.getPattern()).thenReturn(ExchangePattern.InOut);

    // act
    assertThatCode(() -> endpointProcessorProxySubject.process(exchange, callback))
        .doesNotThrowAnyException();

    // assert
    verify(processor, times(0)).process(exchange);
  }
}
