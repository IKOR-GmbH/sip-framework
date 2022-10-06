package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.SIPJmsTextMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.util.TestKitHelper;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.exceptions.UnsupportedJmsHeaderException;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.component.jms.JmsBinding;
import org.apache.camel.component.jms.JmsConsumer;
import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.component.jms.JmsMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JmsRouteInvokerTest {

  private static final String ROUTE_ID = "routeId";

  private JmsRouteInvoker subject;
  private Exchange inputExchange;
  private Exchange actualJmsExchange;
  private Processor processor;

  @BeforeEach
  void setup() {
    ExtendedCamelContext camelContext = mock(ExtendedCamelContext.class);
    subject = new JmsRouteInvoker(camelContext);

    inputExchange = TestKitHelper.parseExchangeProperties(null, camelContext);
    inputExchange.setProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, ROUTE_ID);

    Route route = mock(Route.class);
    JmsConsumer jmsConsumer = mock(JmsConsumer.class);
    JmsEndpoint jmsEndpoint = mock(JmsEndpoint.class);
    JmsBinding jmsBinding = mock(JmsBinding.class);
    processor = mock(Processor.class);
    when(camelContext.getRoute(ROUTE_ID)).thenReturn(route);
    when(route.getConsumer()).thenReturn(jmsConsumer);
    when(jmsConsumer.getEndpoint()).thenReturn(jmsEndpoint);
    when(jmsConsumer.getProcessor()).thenReturn(processor);
    when(jmsEndpoint.getBinding()).thenReturn(jmsBinding);

    actualJmsExchange = TestKitHelper.parseExchangeProperties(null, camelContext);
    when(jmsConsumer.createExchange(false)).thenReturn(actualJmsExchange);
  }

  @Test
  void GIVEN_noInputs_WHEN_invoke_THEN_verifyProcessorCallAndDefaultBodyAndHeaderValues()
      throws Exception {
    // act
    subject.invoke(inputExchange);

    // assert
    verify(processor, times(1)).process(actualJmsExchange);
    assertThat(actualJmsExchange.getMessage().getBody()).isNull();
    assertThat(((JmsMessage) actualJmsExchange.getMessage()).getJmsMessage().getJMSPriority())
        .isEqualTo(4);
    assertThat(((JmsMessage) actualJmsExchange.getMessage()).getJmsMessage().getJMSDeliveryMode())
        .isEqualTo(2);
    assertThat(((JmsMessage) actualJmsExchange.getMessage()).getJmsMessage().getJMSExpiration())
        .isZero();
  }

  @Test
  void GIVEN_headerInputs_WHEN_invoke_THEN_verifyProcessorCallAndBodyAndHeaderValues()
      throws Exception {
    // arrange
    inputExchange.getMessage().setHeader(JMS_PRIORITY, 6);
    inputExchange.getMessage().setHeader(JMS_DELIVERY_MODE, 1);
    inputExchange.getMessage().setHeader("CustomHeader", "test");

    // act
    subject.invoke(inputExchange);

    // assert
    verify(processor, times(1)).process(actualJmsExchange);
    assertThat(((JmsMessage) actualJmsExchange.getMessage()).getJmsMessage().getJMSPriority())
        .isEqualTo(6);
    assertThat(((JmsMessage) actualJmsExchange.getMessage()).getJmsMessage().getJMSDeliveryMode())
        .isEqualTo(1);
    assertThat(
            ((JmsMessage) actualJmsExchange.getMessage())
                .getJmsMessage()
                .getObjectProperty("CustomHeader"))
        .isEqualTo("test");
  }

  @Test
  void GIVEN_forbiddenHeaderInputs_WHEN_invoke_THEN_expectUnsupportedJmsHeaderException()
      throws Exception {
    // arrange
    inputExchange.getMessage().setHeader(JMS_DESTINATION, "test destination");

    // act & assert
    assertThrows(UnsupportedJmsHeaderException.class, () -> subject.invoke(inputExchange));
  }
}
