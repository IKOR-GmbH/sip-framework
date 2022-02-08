package de.ikor.sip.foundation.core.proxies;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import java.util.ArrayList;
import java.util.List;
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

  private static final String PROXY_ID = "proxy";

  @BeforeEach
  void setup() {
    namedNode = mock(NamedNode.class);
    processor = mock(Processor.class);
    proxyExtension = mock(ProxyExtension.class);
    ArrayList<ProxyExtension> exts = new ArrayList<>();
    exts.add(proxyExtension);
    proxyExtensions = exts;
    callback = mock(AsyncCallback.class);
    processorProxySubject = new ProcessorProxy(namedNode, processor, false, proxyExtensions);
  }

  @Test
  void process_executeMock() {
    // arrange
    ExtendedExchange exchange = mock(ExtendedExchange.class, RETURNS_DEEP_STUBS);
    when(exchange.getIn().getHeader("proxy-modes", String.class))
        .thenReturn("{\"" + PROXY_ID + "\": [\"mock\"]}");
    when(namedNode.getId()).thenReturn(PROXY_ID);
    ExchangePattern exchangePattern = ExchangePattern.InOut;
    when(exchange.getPattern()).thenReturn(exchangePattern);

    // act
    processorProxySubject.mock(exchange1 -> exchange1);

    assertThatCode(() -> processorProxySubject.process(exchange, callback))
        .doesNotThrowAnyException();
  }

  @Test
  void process_executeProcessExchange() throws Exception {
    // arrange
    Exchange exchange = mock(Exchange.class, RETURNS_DEEP_STUBS);
    when(exchange.getIn().getHeader("proxy-modes", String.class)).thenReturn(null);
    when(exchange.getIn().getHeader("tracingId", String.class)).thenReturn("id");

    // act
    when(namedNode.getId()).thenReturn(PROXY_ID);
    doNothing().when(processor).process(any());
    when(proxyExtension.isApplicable(any(), any())).thenReturn(true);
    doNothing().when(proxyExtension).run(any(), any());

    assertThatCode(() -> processorProxySubject.process(exchange, callback))
        .doesNotThrowAnyException();
  }

  @Test
  void process_throwIllegalArgument() {
    // arrange
    Exchange exchange = mock(Exchange.class, RETURNS_DEEP_STUBS);
    when(exchange.getIn().getHeader("proxy-modes", String.class))
        .thenReturn("{\"" + PROXY_ID + "\": [\"mock\"]");

    // assert
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> processorProxySubject.process(exchange, callback));
  }

  @Test
  void process_throwMockMissingFunctionException() {
    // arrange
    ExtendedExchange exchange = mock(ExtendedExchange.class, RETURNS_DEEP_STUBS);
    when(exchange.getIn().getHeader("proxy-modes", String.class))
        .thenReturn("{\"" + PROXY_ID + "\": [\"mock\"]}");
    when(namedNode.getId()).thenReturn(PROXY_ID);

    // assert
    assertThatExceptionOfType(MockMissingFunctionException.class)
        .isThrownBy(() -> processorProxySubject.process(exchange, callback));
  }
}
