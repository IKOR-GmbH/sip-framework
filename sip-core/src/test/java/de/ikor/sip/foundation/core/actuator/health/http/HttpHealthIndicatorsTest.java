package de.ikor.sip.foundation.core.actuator.health.http;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.camel.Endpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

class HttpHealthIndicatorsTest {

  Endpoint endpoint;
  private static final String ENDPOINT_URI = "https://www.google.com/";

  @BeforeEach
  void setUp() {
    endpoint = mock(Endpoint.class);
  }

  @Test
  void alwaysHealthy() {
    assertEquals(Health.up().build(), HttpHealthIndicators.alwaysHealthy());
  }

  @Test
  void When_HttpHealthIndicatorIsAlwaysUnknown_Expect_StatusToBeUnknown() {
    // act
    when(endpoint.getEndpointKey()).thenReturn(ENDPOINT_URI);

    // assert
    assertThat(HttpHealthIndicators.alwaysUnknown(endpoint).getStatus()).isEqualTo(Status.UNKNOWN);
  }

  @Test
  void When_HttpHealthIndicatorIsUrlHealthIndicator_Expect_StatusToBeUp() {

    // act
    when(endpoint.getEndpointKey()).thenReturn(ENDPOINT_URI);
    Health health = HttpHealthIndicators.urlHealthIndicator(endpoint);

    // assert
    assertThat(health.getStatus()).isEqualTo(Status.UP);
  }
}
