package de.ikor.sip.foundation.testkit.workflow.whenphase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.util.Optional;
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
    ExecutionWrapper subject = new ExecutionWrapper(TEST_NAME, inputExchange, routeInvoker);
    when(routeInvoker.invoke(any(Exchange.class))).thenReturn(Optional.of(inputExchange));

    // act
    Optional<Exchange> actual = subject.execute();

    // assert
    if (actual.isPresent()) {
      assertThat(actual.get().getMessage().getHeader(RouteInvoker.TEST_NAME_HEADER))
          .isEqualTo(TEST_NAME);
      assertThat(
              actual.get().getMessage().getHeader(ProcessorProxy.TEST_MODE_HEADER, Boolean.class))
          .isTrue();
      assertThat(actual.get().getMessage().getBody()).isNull();
    }
  }

  private Exchange createEmptyExchange() {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }
}
