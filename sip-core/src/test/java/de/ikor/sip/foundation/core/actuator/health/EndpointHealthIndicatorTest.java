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

  private EndpointHealthIndicator subject;
  private Endpoint endpoint;
  private static final String ENDPOINT_URI = "uri";

  @BeforeEach
  void setup() {
    endpoint = mock(Endpoint.class);
  }

  @Test
  void When_name_Expect_HealthIndicatorNameMatchingEndpointURI() {
    // arrange
    subject = new EndpointHealthIndicator(endpoint, null);

    // act
    when(endpoint.getEndpointUri()).thenReturn(ENDPOINT_URI);

    // assert
    assertThat(subject.name()).isEqualTo(ENDPOINT_URI);
  }

  @Test
  void When_healthAndHealthFunctionExist_Expect_IndicatorHealthMatchingHealthFunctionHealth() {
    // arrange
    Health health = Health.up().build();
    Function<Endpoint, Health> healthFunction = endpoint1 -> health;
    subject = new EndpointHealthIndicator(endpoint, healthFunction);

    // assert
    assertThat(subject.health()).isEqualTo(health);
  }
}
