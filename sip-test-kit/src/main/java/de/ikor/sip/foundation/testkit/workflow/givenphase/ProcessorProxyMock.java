package de.ikor.sip.foundation.testkit.workflow.givenphase;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.core.proxies.ProcessorProxyRegistry;
import de.ikor.sip.foundation.testkit.exception.ExceptionType;
import de.ikor.sip.foundation.testkit.exception.TestCaseInitializationException;
import de.ikor.sip.foundation.testkit.workflow.TestExecutionStatus;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/** Creates and defines behaviour for Camel based external service mocks */
@Slf4j
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ProcessorProxyMock extends Mock {
  private ProcessorProxy proxy;
  private final ProcessorProxyRegistry proxyRegistry;

  /**
   * Sets a mock operation on a proxy
   *
   * @param testExecutionStatus that the mock should fill with details of the test run
   */
  @Override
  public void setBehavior(TestExecutionStatus testExecutionStatus) {
    proxy = getMockProxy(getId());
    proxy.mock(this.createOperation(returnExchange, proxy.determineConvertingToBytes()));
  }

  @Override
  public void clear() {
    // If execution is stopped by error before proxy is created, proxy reference can point to null
    if (proxy != null) {
      proxy.reset();
      proxy.mock(exchange -> exchange);
    }
  }

  private ProcessorProxy getMockProxy(String processorId) {
    return proxyRegistry
        .getProxy(processorId)
        .orElseThrow(
            () ->
                new TestCaseInitializationException(
                    "There is no " + processorId + " proxy in the application",
                    ExceptionType.MOCK));
  }

  private UnaryOperator<Exchange> createOperation(Exchange returnExchange, boolean convertToBytes) {
    return exchange -> {
      if (convertToBytes) {
        InputStream targetStream =
            new ByteArrayInputStream(returnExchange.getMessage().getBody(String.class).getBytes());
        exchange.getMessage().setBody(targetStream);
      } else {
        exchange.getMessage().setBody(returnExchange.getMessage().getBody());
      }
      return exchange;
    };
  }
}
