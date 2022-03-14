package de.ikor.sip.foundation.testkit.workflow.givenphase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.core.proxies.ProcessorProxyRegistry;
import de.ikor.sip.foundation.testkit.exception.TestCaseInitializationException;
import de.ikor.sip.foundation.testkit.workflow.TestExecutionStatus;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ProcessorProxyMockTest {

  private static final String PROXY_ID = "id";
  private static final String PROXY_FIELD_NAME = "proxy";

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
  void When_setBehavior_With_ExistingProxy_Expect_ProxySet() {
    // arrange
    when(proxyRegistry.getProxy(PROXY_ID)).thenReturn(Optional.of(proxy));

    // act
    subject.setBehavior(new TestExecutionStatus());

    // assert
    assertThat(ReflectionTestUtils.getField(subject, PROXY_FIELD_NAME)).isEqualTo(proxy);
  }

  @Test
  void When_setBehavior_With_ProxyMissing_Then_TestCaseInitializationException() {
    // act + assert
    TestExecutionStatus testExecutionStatus = new TestExecutionStatus();
    assertThatThrownBy(() -> subject.setBehavior(testExecutionStatus))
        .isInstanceOf(TestCaseInitializationException.class);
  }

  @Test
  void When_clear_With_SetProxy_Then_ProxyReset() {
    // arrange
    ReflectionTestUtils.setField(subject, PROXY_FIELD_NAME, proxy);

    // act
    subject.clear();

    // assert
    verify(proxy, times(1)).reset();
    verify(proxy, times(1)).mock(any());
  }

  @Test
  void When_clear_With_NullProxy_Expect_NoException() {
    // act + assert
    assertThatNoException().isThrownBy(() -> subject.clear());
    assertThat(ReflectionTestUtils.getField(subject, PROXY_FIELD_NAME)).isNull();
  }
}
