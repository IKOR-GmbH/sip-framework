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

  @BeforeEach
  void setup() {
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    subject = new DefaultRouteInvoker(camelContext);
  }

  @Test
  void GIVEN_mockExchange_WHEN_invoke_THEN_returnEmptyExchange() {
    // arrange
    Exchange inputExchange = mock(Exchange.class);

    // act
    Exchange actual = subject.invoke(inputExchange);

    // assert
    assertThat(actual.getMessage().getBody()).isNull();
    assertThat(actual.getMessage().getHeaders()).isEmpty();
  }

  @Test
  void GIVEN_mockEndpoint_WHEN_isApplicable_THEN_returnEmptyExchange() {
    // arrange
    Endpoint endpoint = mock(Endpoint.class);

    // act
    boolean actual = subject.isApplicable(endpoint);

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
