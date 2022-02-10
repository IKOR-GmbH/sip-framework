package de.ikor.sip.foundation.core.proxies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.camel.Endpoint;
import org.apache.camel.NamedNode;
import org.apache.camel.processor.SendProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddProxyInterceptStrategyTest {
  private static final String PROCESSOR_ID = "processorId";
  private AddProxyInterceptStrategy addProxyInterceptStrategy;
  private ProcessorProxyRegistry proxyRegistry = new ProcessorProxyRegistry();
  private List<ProxyExtension> extensions = new ArrayList<>();
  @Mock private NamedNode definition;

  @BeforeEach
  public void setup() {
    addProxyInterceptStrategy = new AddProxyInterceptStrategy(proxyRegistry, extensions);
    when(definition.getId()).thenReturn(PROCESSOR_ID);
  }

  @Test
  void WHEN_wrapProcessorInInterceptors_WITH_validParams_THEN_wrapped() throws Exception {
    // arrange

    // act
    addProxyInterceptStrategy.wrapProcessorInInterceptors(null, definition, null, null);

    // assert
    Optional<ProcessorProxy> proxy = proxyRegistry.getProxy(PROCESSOR_ID);
    assertThat(proxy).isPresent();
    assertThat(proxyRegistry.getProxies()).hasSize(1);
  }

  @Test
  void WHEN_wrapEndpointProcessor_WITH_validParams_THEN_endpointProcessorTrue() throws Exception {
    // arrange
    SendProcessor outgoingProcessor = mock(SendProcessor.class);
    Endpoint outgoingEndpoint = mock(Endpoint.class);
    when(outgoingProcessor.getEndpoint()).thenReturn(outgoingEndpoint);
    when(outgoingEndpoint.getEndpointUri()).thenReturn("file://test.txt");
    // act
    addProxyInterceptStrategy.wrapProcessorInInterceptors(
        null, definition, outgoingProcessor, outgoingProcessor);

    // assert
    Optional<ProcessorProxy> proxy = proxyRegistry.getProxy(PROCESSOR_ID);
    assertThat(proxy).isPresent();
    assertThat(proxy.get().isEndpointProcessor()).isTrue();
  }

  @Test
  void WHEN_wrapEndpointProcessor_WITH_ignoredEndpoint_THEN_endpointProcessorFalse()
      throws Exception {
    // arrange
    SendProcessor outgoingProcessor = mock(SendProcessor.class);
    Endpoint outgoingEndpoint = mock(Endpoint.class);
    when(outgoingProcessor.getEndpoint()).thenReturn(outgoingEndpoint);
    when(outgoingEndpoint.getEndpointUri()).thenReturn("sipmc:middleComponent");
    // act
    addProxyInterceptStrategy.wrapProcessorInInterceptors(
        null, definition, outgoingProcessor, outgoingProcessor);

    // assert
    Optional<ProcessorProxy> proxy = proxyRegistry.getProxy(PROCESSOR_ID);
    assertThat(proxy).isPresent();
    assertThat(proxy.get().isEndpointProcessor()).isFalse();
  }
}
