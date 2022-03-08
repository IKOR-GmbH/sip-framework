package de.ikor.sip.testframework.workflow.givenphase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import de.ikor.sip.testframework.workflow.givenphase.Mock;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MockTest {

  @Test
  void When_getID_Expect_correctID() {
    // arrange
    Mock aMock = mock(Mock.class, CALLS_REAL_METHODS);
    String alias = "alias";
    Exchange exchange = mock(Exchange.class);
    when(exchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, String.class)).thenReturn(alias);
    aMock.setReturnExchange(exchange);

    // act + assert
    assertThat(aMock.getId()).isEqualTo(alias);
  }
}
