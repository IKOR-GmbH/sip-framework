package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultRouteInvokerTest {

  private DefaultRouteInvoker subject;
  private ExtendedCamelContext camelContext;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
    subject = new DefaultRouteInvoker(camelContext, mock(Endpoint.class));
  }

  @Test
  void GIVEN_mockExchange_WHEN_invoke_THEN_returnEmptyExchange() {
    // arrange
    Exchange inputeExchange = mock(Exchange.class);

    // act
    Exchange actual = subject.invoke(inputeExchange);

    // assert
    assertThat(actual.getMessage().getBody()).isNull();
    assertThat(actual.getMessage().getHeaders()).isEmpty();
  }

  @Test
  void GIVEN_mockEndpoint_WHEN_matchEndpoint_THEN_returnEmptyExchange() {
    // arrange
    Endpoint endpoint = mock(Endpoint.class);

    // act
    boolean actual = subject.matchEndpoint(endpoint);

    // assert
    assertThat(actual).isFalse();
  }

  @Test
  void GIVEN_mockEndpoint_WHEN_setEndpoint_THEN_returnEmptyExchange() {
    // arrange
    Endpoint endpoint = mock(Endpoint.class);

    // act & assert
    assertThat(subject.setEndpoint(endpoint)).isInstanceOf(DefaultRouteInvoker.class);
  }
}
