package de.ikor.sip.foundation.core.actuator.health;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.apache.camel.Endpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EndpointHealthRegistryTest {

  EndpointHealthRegistry endpointHealthRegistry;
  private static final String ENDPOINT_URI = "test";
  private static final String ENDPOINT_ID = "testId";

  @BeforeEach
  void setUp() {
    endpointHealthRegistry = new EndpointHealthRegistry();
  }

  @Test
  void register() {

    // assert
    assertDoesNotThrow(() -> endpointHealthRegistry.register(ENDPOINT_URI, null));
  }

  @Test
  void registerById() {

    // assert
    assertDoesNotThrow(() -> endpointHealthRegistry.registerById(ENDPOINT_ID, null));
  }

  @Test
  void healthIndicator_isPresent() {
    // arrange
    Endpoint endpoint = mock(Endpoint.class);
    when(endpoint.getEndpointUri()).thenReturn(ENDPOINT_URI);

    // act
    endpointHealthRegistry.register(ENDPOINT_URI, null);
    Optional<EndpointHealthIndicator> healthIndicator =
        endpointHealthRegistry.healthIndicator(endpoint);

    // assert
    assertThat(healthIndicator).isNotNull().isPresent();
  }

  @Test
  void healthIndicator_notPresent() {
    // arrange
    Endpoint endpoint = mock(Endpoint.class);
    when(endpoint.getEndpointUri()).thenReturn(ENDPOINT_URI);

    // act
    Optional<EndpointHealthIndicator> healthIndicator =
        endpointHealthRegistry.healthIndicator(endpoint);

    // assert
    assertThat(healthIndicator).isNotNull().isNotPresent();
  }
}
