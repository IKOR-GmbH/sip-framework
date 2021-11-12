package de.ikor.sip.foundation.core.actuator.health;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ServiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.NamedContributor;

class CamelEndpointHealthMonitorTest {

  CamelContext camelContext;
  CamelEndpointHealthMonitor camelEndpointHealthMonitor;
  EndpointHealthRegistry endpointHealthRegistry;

  @BeforeEach
  void setup() {
    endpointHealthRegistry = mock(EndpointHealthRegistry.class);
    camelContext = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    camelEndpointHealthMonitor =
        new CamelEndpointHealthMonitor(camelContext, endpointHealthRegistry);
  }

  @Test
  void setupEndpointHealthIndicators() {
    // arrange
    EndpointHealthIndicator endpointHealthIndicator = mock(EndpointHealthIndicator.class);
    when(camelContext.getEndpoints()).thenReturn(Arrays.asList(mock(Endpoint.class)));
    when(camelContext.getStatus()).thenReturn(ServiceStatus.Started);
    when(endpointHealthIndicator.name()).thenReturn("test");
    when(endpointHealthRegistry.healthIndicator(any()))
        .thenReturn(Optional.of(endpointHealthIndicator));

    // act
    Iterator<NamedContributor<HealthContributor>> iterator =
        camelEndpointHealthMonitor.setupEndpointHealthIndicators();

    // assert
    assertThat(iterator).isNotNull();
    assertThat(camelEndpointHealthMonitor.getHealthIndicators()).isNotNull();
    assertThat(camelEndpointHealthMonitor.healthIndicators()).isNotNull();
  }
}
