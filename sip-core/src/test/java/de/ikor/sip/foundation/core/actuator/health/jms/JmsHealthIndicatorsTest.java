package de.ikor.sip.foundation.core.actuator.health.jms;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.actuator.common.IntegrationManagementException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import org.apache.camel.Endpoint;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsEndpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

class JmsHealthIndicatorsTest {

  JmsEndpoint endpoint;
  ConnectionFactory connectionFactory;

  @BeforeEach
  void setUp() {

    endpoint = mock(JmsEndpoint.class);
    JmsComponent component = mock(JmsComponent.class);
    connectionFactory = mock(ConnectionFactory.class);

    when(endpoint.getComponent()).thenReturn(component);
    when(component.getConnectionFactory()).thenReturn(connectionFactory);
  }

  @Test
  void connectionManageable_statusUp() throws JMSException {
    // arrange
    Connection connection = mock(Connection.class);
    when(connectionFactory.createConnection()).thenReturn(connection);

    // assert
    assertThat(JmsHealthIndicators.connectionManageable(endpoint).getStatus()).isEqualTo(Status.UP);
  }

  @Test
  void connectionManageable_statusDown() throws JMSException {
    // arrange
    when(connectionFactory.createConnection()).thenThrow(JMSException.class);

    // assert
    assertThat(JmsHealthIndicators.connectionManageable(endpoint).getStatus())
        .isEqualTo(Status.DOWN);
  }

  @Test
  void connectionManageable_typeMismatch() {
    // arrange
    Endpoint endpoint1 = mock(Endpoint.class);

    // assert
    assertThatThrownBy(() -> JmsHealthIndicators.connectionManageable(endpoint1))
        .isInstanceOf(IntegrationManagementException.class);
  }
}
