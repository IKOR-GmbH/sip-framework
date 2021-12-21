package de.ikor.sip.foundation.core.actuator.health;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.processor.SendProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HealthMonitorSetupTest {

  EndpointHealthRegistry endpointHealthRegistry;
  HealthMonitorSetup subject;

  private static final String ENDPOINT_URI = "endpoint";
  private static final String PROCESSOR_ID = "processor";

  @Mock CamelContext camelContext;

  @Mock CamelEndpointHealthMonitor camelEndpointHealthMonitor;

  @BeforeEach
  void setUp() {
    endpointHealthRegistry = new EndpointHealthRegistry();
    subject =
        new HealthMonitorSetup(camelContext, endpointHealthRegistry, camelEndpointHealthMonitor);

    when(camelContext.getProcessor(PROCESSOR_ID)).thenReturn(mock(SendProcessor.class));
    when(((SendProcessor) camelContext.getProcessor(PROCESSOR_ID)).getDestination())
        .thenReturn(mock(Endpoint.class));
    when(((SendProcessor) camelContext.getProcessor(PROCESSOR_ID))
            .getDestination()
            .getEndpointUri())
        .thenReturn(ENDPOINT_URI);
  }

  @Test
  void When_setupCamelEndpointHealthMonitor_Expect_MatchersAndIndicatorMatcherNotEmpty() {
    // arrange
    endpointHealthRegistry.registerById(PROCESSOR_ID, null);

    // act
    subject.setupCamelEndpointHealthMonitor();

    // assert
    assertThat(endpointHealthRegistry.getMatchersByProcessorId().size()).isEqualTo(1);
    assertThat(endpointHealthRegistry.getHealthIndicatorMatchers().size()).isEqualTo(1);
  }

  @Test
  void
      When_setupCamelEndpointHealthMonitorAndIdAlreadyRegistered_Expect_ThrowDuplicateUriPatternError() {
    // arrange
    endpointHealthRegistry.registerById(PROCESSOR_ID, null);
    endpointHealthRegistry.registerById(PROCESSOR_ID, null);

    // assert
    assertThatExceptionOfType(DuplicateUriPatternError.class)
        .isThrownBy(() -> subject.setupCamelEndpointHealthMonitor());
  }
}
