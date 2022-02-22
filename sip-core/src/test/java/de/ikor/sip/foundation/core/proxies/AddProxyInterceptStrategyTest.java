package de.ikor.sip.foundation.core.proxies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.camel.NamedNode;
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
}
