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

  private EndpointHealthIndicator endpointHealthIndicator;
  private Endpoint endpoint;
  private Health health;
  private static final String ENDPOINT_URI = "uri";

  @BeforeEach
  void setup() {
    endpoint = mock(Endpoint.class);
    health = Health.up().build();
    Function<Endpoint, Health> healthFunction =
        endpoint -> {
          return health;
        };
    endpointHealthIndicator = new EndpointHealthIndicator(endpoint, healthFunction);
  }

  @Test
  void WHEN_name_EXPECT_endpointNameReturned() {
    // arrange
    when(endpoint.getEndpointUri()).thenReturn(ENDPOINT_URI);

    // act
    String nameSubject = endpointHealthIndicator.name();

    // assert
    assertThat(nameSubject).isEqualTo(ENDPOINT_URI);
  }

  @Test
  void WHEN_health_WITH_NoHealthCalculation_THEN_NoHealth() {
    // act
    Health healthSubject = endpointHealthIndicator.health();

    // assert
    assertThat(healthSubject).isNull();
  }

  @Test
  void WHEN_health_WITH_HealthCalculation_THEN_ReturnHealth() {
    // act
    endpointHealthIndicator.executeHealthCheck();
    Health healthSubject = endpointHealthIndicator.health();

    // assert
    assertThat(healthSubject).isEqualTo(health);
  }

  @Test
  void WHEN_executeHealthCheck_EXPECT_HealthIsCalculated() {
    // act
    endpointHealthIndicator.executeHealthCheck();
    Health healthSubject = endpointHealthIndicator.getHealth(false);

    // assert
    assertThat(healthSubject).isEqualTo(health);
  }
}
