package de.ikor.sip.foundation.core.proxies;

import static de.ikor.sip.foundation.core.proxies.ProcessorProxy.TEST_MODE_HEADER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import java.util.List;
import java.util.function.UnaryOperator;
import org.apache.camel.*;
import org.apache.camel.processor.SendProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessorProxyTest {

  ProcessorProxy processorProxySubject;
  ProcessorProxy processorProxySubjectOutgoing;
  List<ProxyExtension> proxyExtensions;
  NamedNode namedNode;
  Processor processor;
  ProxyExtension proxyExtension;
  AsyncCallback callback;
  Exchange exchange;
  SendProcessor outgoingProcessor;
  Endpoint outgoingEndpoint;

  private static final String PROXY_ID = "proxy";

  @BeforeEach
  void setup() {
    namedNode = mock(NamedNode.class);
    processor = mock(Processor.class);
    proxyExtension = mock(ProxyExtension.class);
    proxyExtensions = List.of(proxyExtension);
    callback = mock(AsyncCallback.class);
    exchange = mock(ExtendedExchange.class, RETURNS_DEEP_STUBS);
    processorProxySubject = new ProcessorProxy(namedNode, processor, processor, proxyExtensions);
    outgoingProcessor = mock(SendProcessor.class);
    outgoingEndpoint = mock(Endpoint.class);
    when(outgoingProcessor.getEndpoint()).thenReturn(outgoingEndpoint);
    processorProxySubjectOutgoing =
        new ProcessorProxy(namedNode, outgoingProcessor, outgoingProcessor, proxyExtensions);
  }

  private void putProxyInTestMode() {
    when(exchange.getIn().getHeader(TEST_MODE_HEADER, String.class)).thenReturn("true");
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
    verify(proxyExtension, times(0)).run(any(), any());
    verify(processor, times(1)).process(exchange);
  }

  @Test
  void process_executeMock() throws Exception {
    // arrange
    putProxyInTestMode();

    when(exchange.getPattern()).thenReturn(ExchangePattern.InOut);

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
  void process_executeProcessExchangeInTestMode() throws Exception {
    // arrange
    putProxyInTestMode();
    when(exchange.getPattern()).thenReturn(ExchangePattern.InOut);

    // act
    assertThatCode(() -> processorProxySubject.process(exchange, callback))
        .doesNotThrowAnyException();

    // assert
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
  void process_endpointProcessorTestModeAfterRemovingMockFunction() throws Exception {
    // arrange
    putProxyInTestMode();
    when(exchange.getPattern()).thenReturn(ExchangePattern.InOut);
    UnaryOperator<Exchange> mockFunction = mock(UnaryOperator.class);
    when(mockFunction.apply(exchange)).thenReturn(exchange);
    // act
    processorProxySubjectOutgoing.mock(mockFunction);
    processorProxySubjectOutgoing.reset();

    assertThatCode(() -> processorProxySubjectOutgoing.process(exchange, callback))
        .doesNotThrowAnyException();

    // assert
    verify(outgoingProcessor, times(1)).process(exchange);
    verify(mockFunction, times(0)).apply(exchange);
  }

  @Test
  void isEndpointProcessor_regularProcessor() throws Exception {
    // assert
    assertThat(processorProxySubject.isEndpointProcessor()).isFalse();
  }

  @Test
  void isEndpointProcessor_regularEndpointProcessor() throws Exception {
    // arrange
    when(outgoingEndpoint.getEndpointUri()).thenReturn("file://test.txt");

    // assert
    assertThat(processorProxySubjectOutgoing.isEndpointProcessor()).isTrue();
  }

  @Test
  void isEndpointProcessor_ignoredEndpointProcessor() throws Exception {
    // arange
    when(outgoingEndpoint.getEndpointUri()).thenReturn("sipmc:middleComponent");

    // assert
    assertThat(processorProxySubjectOutgoing.isEndpointProcessor()).isFalse();
  }
}
