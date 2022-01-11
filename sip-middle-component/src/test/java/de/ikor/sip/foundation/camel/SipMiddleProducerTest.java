package de.ikor.sip.foundation.camel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SipMiddleProducerTest {

  @Mock private Endpoint endpoint;
  @Mock private Producer producer;
  private SipMiddleProducer subject;

  @BeforeEach
  void setUp() throws Exception {
    when(endpoint.createProducer()).thenReturn(producer);
    subject = new SipMiddleProducer(endpoint);
  }

  @Test
  void WHEN_doStart_THEN_startProducerCalled() throws Exception {
    // arrange

    // act
    subject.doStart();

    // assert
    verify(producer).start();
  }

  @Test
  void WHEN_doStop_THEN_stopProducerCalled() throws Exception {
    // arrange

    // act
    subject.doStop();

    // assert
    verify(producer).stop();
  }

  @Test
  void WHEN_processExchange_THEN_processExchangeOnProducerCalled() throws Exception {
    // arrange
    Exchange exchange = mock(Exchange.class);

    // act
    subject.process(exchange);

    // assert
    verify(producer).process(exchange);
  }
}
