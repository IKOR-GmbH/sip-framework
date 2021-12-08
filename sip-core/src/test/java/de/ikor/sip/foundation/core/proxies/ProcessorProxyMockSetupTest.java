package de.ikor.sip.foundation.core.proxies;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.extension.ProxyExtension;
import org.apache.camel.Exchange;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@ExtendWith(MockitoExtension.class)
class ProcessorProxyMockSetupTest {

    ProcessorProxyMockSetup processorProxyMockSetup;
    ProcessorProxyRegistry proxyRegistry;
    ProcessorProxyMockRegistry processorProxyMockRegistry;
    ProcessorProxy processorProxy;

    List<ProxyExtension> proxyExtensions;
    NamedNode namedNode;
    Processor processor;
    ProxyExtension proxyExtension;

    private static final String PROCESSOR_ID = "processorId";

    @BeforeEach
    void setUp() {
        proxyRegistry = new ProcessorProxyRegistry();
        processorProxyMockRegistry = new ProcessorProxyMockRegistry();
        processorProxyMockSetup = new ProcessorProxyMockSetup(proxyRegistry, processorProxyMockRegistry);
        setUpProcessorProxy();
    }

    void setUpProcessorProxy() {
        namedNode = mock(NamedNode.class);
        processor = mock(Processor.class);
        proxyExtension = mock(ProxyExtension.class);
        ArrayList<ProxyExtension> exts = new ArrayList<>();
        exts.add(proxyExtension);
        proxyExtensions = exts;
        processorProxy = new ProcessorProxy(namedNode, processor, proxyExtensions);
    }

    @Test
    void mockProcessorProxys() {
        UnaryOperator<Exchange> mockFunctionExample = exchange -> exchange;

        proxyRegistry.register(PROCESSOR_ID, processorProxy);
        processorProxyMockRegistry.registerMock(PROCESSOR_ID, mockFunctionExample);

        processorProxyMockSetup.mockProcessorProxies();

        assertThat(proxyRegistry.getProxy(PROCESSOR_ID).get().getMockFunction()).isEqualTo(mockFunctionExample);
    }

    @Test
    void mockProcessorProxys_whenBadProxyProcessorIdException() {
        UnaryOperator<Exchange> mockFunctionExample = exchange -> exchange;

        processorProxyMockRegistry.registerMock(PROCESSOR_ID, mockFunctionExample);

        assertThatExceptionOfType(BadProxyProcessorIdException.class)
                .isThrownBy(() -> processorProxyMockSetup.mockProcessorProxies());
    }
}
