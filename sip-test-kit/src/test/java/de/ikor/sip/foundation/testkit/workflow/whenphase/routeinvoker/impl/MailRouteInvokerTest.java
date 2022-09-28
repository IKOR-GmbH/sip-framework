package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import org.apache.camel.*;
import org.apache.camel.component.mail.MailConsumer;
import org.apache.camel.component.mail.MailEndpoint;
import org.apache.camel.support.EmptyAsyncCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MailRouteInvokerTest {

  private static final String NODE_ID = "nodeId";
  MailRouteInvoker subject;
  CamelContext camelContext;

  @BeforeEach
  void setup() {
    camelContext = mock(CamelContext.class);
    subject = new MailRouteInvoker(camelContext);
  }

  @Test
  void When_invoke_Expect_ExchangeProcessingInvoked() {
    // arrange
    Exchange exchange = mock(Exchange.class, RETURNS_DEEP_STUBS);
    Route route = mock(Route.class);
    MailConsumer mailConsumer = mock(MailConsumer.class);
    AsyncProcessor asyncProcessor = mock(AsyncProcessor.class);
    when(exchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY)).thenReturn(NODE_ID);
    when(camelContext.getRoute(NODE_ID)).thenReturn(route);
    when(route.getConsumer()).thenReturn(mailConsumer);
    when(mailConsumer.getAsyncProcessor()).thenReturn(asyncProcessor);

    // act
    subject.invoke(exchange);

    // assert
    verify(asyncProcessor, times(1)).process(exchange, EmptyAsyncCallback.get());
  }

  @Test
  void When_isApplicable_Expect_True() {
    // arrange
    MailEndpoint endpoint = mock(MailEndpoint.class);

    // act + assert
    assertThat(subject.isApplicable(endpoint)).isTrue();
  }

  @Test
  void When_isNotApplicable_Expect_False() {
    // arrange
    Endpoint endpoint = mock(Endpoint.class);

    // act + assert
    assertThat(subject.isApplicable(endpoint)).isFalse();
  }
}
