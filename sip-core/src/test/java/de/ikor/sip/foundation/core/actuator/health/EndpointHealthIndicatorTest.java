package de.ikor.sip.foundation.core.actuator.health;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import org.apache.camel.Endpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

class EndpointHealthIndicatorTest {

  private static final String ENDPOINT_NAME = "name";

  EndpointHealthIndicator endpointHealthIndicator;
  Endpoint endpoint;
  Health health;

  @BeforeEach
  void setup() {
    endpoint = mock(Endpoint.class);
    health = Health.up().build();
    Function<Endpoint, Health> healthFunction =
        endpoint1 -> {
          return health;
        };
    endpointHealthIndicator = new EndpointHealthIndicator(endpoint, healthFunction);
  }

  @Test
  void name() {
    // act
    when(endpoint.getEndpointUri()).thenReturn(ENDPOINT_NAME);

    // assert
    assertThat(endpointHealthIndicator.name()).isEqualTo(ENDPOINT_NAME);
  }

  @Test
  void health() {

    // assert
    assertThat(health).isEqualTo(endpointHealthIndicator.health());
  }
}
