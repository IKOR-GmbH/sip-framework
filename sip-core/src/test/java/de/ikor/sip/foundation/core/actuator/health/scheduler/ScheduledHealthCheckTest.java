package de.ikor.sip.foundation.core.actuator.health.scheduler;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.actuator.health.CamelEndpointHealthMonitor;
import de.ikor.sip.foundation.core.actuator.health.EndpointHealthIndicator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.camel.Endpoint;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

class ScheduledHealthCheckTest {

  private static final String ENDPOINT = "endpoint";
  private static final Health HEALTH_STATUS_UP = Health.up().build();

  private CamelEndpointHealthMonitor camelEndpointHealthMonitor;
  private ScheduledHealthCheck scheduledHealthCheckSubject;


  @Test
  void WHEN_scheduledExecution_EXPECT_HealthEndpointsAreCalculated() {
    // arrange
    camelEndpointHealthMonitor = mock(CamelEndpointHealthMonitor.class);
    scheduledHealthCheckSubject = new ScheduledHealthCheck(camelEndpointHealthMonitor);

    Map<String, EndpointHealthIndicator> healthIndicators = new HashMap<>();
    Function<Endpoint, Health> healthFunction = endpoint -> HEALTH_STATUS_UP;
    EndpointHealthIndicator endpointHealthIndicator =
        new EndpointHealthIndicator(mock(Endpoint.class), healthFunction);
    healthIndicators.put(ENDPOINT, endpointHealthIndicator);

    when(camelEndpointHealthMonitor.getHealthIndicators()).thenReturn(healthIndicators);

    // act
    scheduledHealthCheckSubject.scheduledExecution();
    Health healthResult = healthIndicators.get(ENDPOINT).getHealth(false);

    // assert
    assertThat(healthResult).isEqualTo(HEALTH_STATUS_UP);
  }
}
