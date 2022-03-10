package de.ikor.sip.testframework.workflow.whenphase.executor.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.testframework.util.SIPRouteProducerTemplate;
import de.ikor.sip.testframework.workflow.whenphase.executor.Executor;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.Test;

class DefaultTestExecutorTest {

  @Test
  void execute() {
    // arrange
    String testName = "name";
    SIPRouteProducerTemplate sipRouteProducerTemplate = mock(SIPRouteProducerTemplate.class);
    DefaultTestExecutor subject = new DefaultTestExecutor(sipRouteProducerTemplate);
    Exchange request = mock(Exchange.class, RETURNS_DEEP_STUBS);
    Exchange response = mock(Exchange.class);
    Map<String, Object> headers = new HashMap<>();
    when(request.getMessage().getHeaders()).thenReturn(headers);
    when(sipRouteProducerTemplate.requestOnRoute(request)).thenReturn(response);

    // act + assert
    assertThat(subject.execute(request, testName)).isEqualTo(response);
    assertThat(headers)
        .containsEntry(Executor.TEST_NAME_HEADER, testName)
        .containsEntry(ProcessorProxy.TEST_MODE_HEADER, true);
  }
}
