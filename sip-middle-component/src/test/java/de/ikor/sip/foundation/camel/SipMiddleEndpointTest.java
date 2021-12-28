package de.ikor.sip.foundation.camel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spring.boot.SpringBootCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SipMiddleEndpointTest {

  private static final String BASE_URI = "sipmc:foo";
  private static final String TARGET_URI = "seda:foo?multipleConsumers=false";

  @Mock private SpringBootCamelContext context;
  @Mock private SipMiddleComponent component;
  @Mock private Endpoint endpoint;

  private SipMiddleEndpoint subject;

  @BeforeEach
  void setUp() {
    when(component.getCamelContext()).thenReturn(context);
    when(context.getEndpoint(TARGET_URI)).thenReturn(endpoint);

    subject = new SipMiddleEndpoint(BASE_URI, component, TARGET_URI);
  }

  @Test
  void WHEN_createProducer_THEN_middleProducerReturned() throws Exception {
    // act
    Producer result = subject.createProducer();

    // assert
    assertThat(result).isInstanceOf(SipMiddleProducer.class);
    assertThat(result.getEndpoint()).isSameAs(endpoint);
  }

  @Test
  void WHEN_createConsumer_THEN_consumerReturned() throws Exception {
    // arrange
    Processor processor = mock(Processor.class);
    Consumer consumer = mock(Consumer.class);
    when(endpoint.createConsumer(processor)).thenReturn(consumer);

    // act
    Consumer result = subject.createConsumer(processor);

    // assert
    verify(endpoint).createConsumer(processor);
    assertThat(result).isSameAs(consumer);
  }
}
