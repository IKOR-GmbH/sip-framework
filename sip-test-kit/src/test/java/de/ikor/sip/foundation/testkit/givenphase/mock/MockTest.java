package de.ikor.sip.foundation.testkit.givenphase.mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.config.TestCasesConfig;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import org.apache.camel.Exchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MockTest {

  Mock aMock;

  @BeforeEach
  void setUp() {
    aMock = mock(Mock.class, CALLS_REAL_METHODS);
  }

  @Test
  void getID() {
    String alias = "alias";
    Exchange exchange = mock(Exchange.class);
    when(exchange.getProperty(TestCasesConfig.ENDPOINT_ID_EXCHANGE_PROPERTY, String.class))
        .thenReturn(alias);
    aMock.setReturnExchange(exchange);

    assertEquals(alias, aMock.getId());
  }
}
