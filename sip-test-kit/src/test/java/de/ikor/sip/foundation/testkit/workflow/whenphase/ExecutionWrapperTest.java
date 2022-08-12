package de.ikor.sip.foundation.testkit.workflow.whenphase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.Test;

class ExecutionWrapperTest {

  private static final String TEST_NAME = "test";

  private ExtendedCamelContext camelContext;

  @Test
  void GIVEN_mockExchange_WHEN_execute_THEN_validateTestKitHeaders() {
    // arrange
    camelContext = mock(ExtendedCamelContext.class);
    Exchange inputExchange = createEmptyExchange();
    RouteInvoker routeInvoker = mock(RouteInvoker.class);
    Endpoint endpoint = mock(Endpoint.class);
    ExecutionWrapper subject =
        new ExecutionWrapper(TEST_NAME, inputExchange, routeInvoker, endpoint);
    when(routeInvoker.invoke(any(Exchange.class), any(Endpoint.class))).thenReturn(inputExchange);

    // act
    Exchange actual = subject.execute();

    // assert
    assertThat(actual.getMessage().getHeader(RouteInvoker.TEST_NAME_HEADER)).isEqualTo(TEST_NAME);
    assertThat(actual.getMessage().getHeader(ProcessorProxy.TEST_MODE_HEADER, Boolean.class))
        .isTrue();
    assertThat(actual.getMessage().getBody()).isNull();
  }

  private Exchange createEmptyExchange() {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }
}
