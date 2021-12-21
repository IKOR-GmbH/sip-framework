package de.ikor.sip.foundation.core.actuator.health;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ServiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CamelEndpointHealthMonitorTest {

  private CamelContext camelContext;
  private CamelEndpointHealthMonitor subject;
  private EndpointHealthRegistry endpointHealthRegistry;

  @BeforeEach
  void setup() {
    endpointHealthRegistry = mock(EndpointHealthRegistry.class);
    camelContext = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    subject = new CamelEndpointHealthMonitor(camelContext, endpointHealthRegistry);
  }

  @Test
  void
      When_setupEndpointHealthIndicatorsAndEndpointHealthIndicatorExists_Expect_HealthIndicatorsIsNotEmpty() {
    // arrange
    EndpointHealthIndicator endpointHealthIndicator = mock(EndpointHealthIndicator.class);
    when(camelContext.getEndpoints()).thenReturn(Arrays.asList(mock(Endpoint.class)));
    when(camelContext.getStatus()).thenReturn(ServiceStatus.Started);
    when(endpointHealthIndicator.name()).thenReturn("test");
    when(endpointHealthRegistry.healthIndicator(any()))
        .thenReturn(Optional.of(endpointHealthIndicator));

    // act
    subject.setupEndpointHealthIndicators();

    // assert
    assertThat(subject.getHealthIndicators()).isNotEmpty();
  }

  @Test
  void
      When_setupEndpointHealthIndicatorsAndNoEndpointHealthIndicatorsExist_Expect_HealthIndicatorsIsEmpty() {
    // arrange
    when(camelContext.getEndpoints()).thenReturn(Arrays.asList(mock(Endpoint.class)));
    when(camelContext.getStatus()).thenReturn(ServiceStatus.Started);

    // act
    subject.setupEndpointHealthIndicators();

    // assert
    assertThat(subject.getHealthIndicators()).isEmpty();
  }
}
