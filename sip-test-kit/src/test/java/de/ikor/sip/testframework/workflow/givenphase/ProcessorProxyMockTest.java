package de.ikor.sip.testframework.workflow.givenphase;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.core.proxies.ProcessorProxyRegistry;
import de.ikor.sip.testframework.workflow.TestExecutionStatus;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProcessorProxyMockTest {

    private static final String PROXY_ID = "id";

    private ProcessorProxyMock subject;
    private ProcessorProxy proxy;
    private ProcessorProxyRegistry proxyRegistry;
    private Exchange returnExchange;

    @BeforeEach
    void setup() {
        proxy = mock(ProcessorProxy.class);
        proxyRegistry = mock(ProcessorProxyRegistry.class);
        returnExchange = mock(Exchange.class, RETURNS_DEEP_STUBS);
        subject = new ProcessorProxyMock(proxyRegistry);
        subject.setReturnExchange(returnExchange);
        when(returnExchange.getProperty("connectionAlias", String.class)).thenReturn(PROXY_ID);
        when(returnExchange.getMessage().getBody()).thenReturn("body");
    }

    @Test
    void When_setBehavior_Expect_ProxyExists() {
        // arrange
        when(proxyRegistry.getProxy(PROXY_ID)).thenReturn(Optional.of(proxy));

        // act
        subject.setBehavior(new TestExecutionStatus());

        // assert
        assertThat(ReflectionTestUtils.getField(subject, "proxy")).isEqualTo(proxy);
    }

    @Test
    void When_clear_Expect_ProxyReset() {
        // arrange
        ReflectionTestUtils.setField(subject, "proxy", proxy);

        // act
        subject.clear();

        // assert
        verify(proxy, times(1)).reset();
        verify(proxy, times(1)).mock(any());
    }
}