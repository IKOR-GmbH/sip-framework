package de.ikor.sip.foundation.core.proxies;

import static org.assertj.core.api.Assertions.*;

import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.UnaryOperator;

class ProcessorProxyMockRegistryTest {

    ProcessorProxyMockRegistry processorProxyMockRegistry;

    private static final String PROCESSOR_ID = "processorId";

    @BeforeEach
    void setup() {
        processorProxyMockRegistry = new ProcessorProxyMockRegistry();
    }

    @Test
    void registerMock() {
        UnaryOperator<Exchange> mockFunctionExample = exchange -> exchange;
        processorProxyMockRegistry.registerMock(PROCESSOR_ID, mockFunctionExample);

        assertThat(processorProxyMockRegistry.getProxyMocks().get(PROCESSOR_ID).getProcessorId())
                .isEqualTo(PROCESSOR_ID);
        assertThat(processorProxyMockRegistry.getProxyMocks().get(PROCESSOR_ID).getMockFunction())
                .isEqualTo(mockFunctionExample);
    }

    @Test
    void registerMock_whenBadProxyProcessorIdException() {
        UnaryOperator<Exchange> mockFunctionExample = exchange -> exchange;
        processorProxyMockRegistry.registerMock(PROCESSOR_ID, mockFunctionExample);

        assertThatExceptionOfType(BadProxyProcessorIdException.class)
                .isThrownBy(() -> processorProxyMockRegistry.registerMock(null, mockFunctionExample));
        assertThatExceptionOfType(BadProxyProcessorIdException.class)
                .isThrownBy(() -> processorProxyMockRegistry.registerMock(PROCESSOR_ID, mockFunctionExample));
    }
}
