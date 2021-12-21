package de.ikor.sip.foundation.core.actuator.health.http;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.camel.Endpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

class HttpHealthIndicatorsTest {

  private Endpoint endpoint;
  private static final String ENDPOINT_URI = "https://www.google.com/";

  @BeforeEach
  void setUp() {
    endpoint = mock(Endpoint.class);
  }

  @Test
  void When_alwaysHealthy_Expect_StatusUp() {
    // assert
    assertThat(HttpHealthIndicators.alwaysHealthy().getStatus()).isEqualTo(Status.UP);
  }

  @Test
  void When_alwaysUnknown_Expect_StatusUnknown() {
    // arrange
    when(endpoint.getEndpointKey()).thenReturn(ENDPOINT_URI);

    // act
    Health subject = HttpHealthIndicators.alwaysUnknown(endpoint);

    // assert
    assertThat(subject.getStatus()).isEqualTo(Status.UNKNOWN);
    assertThat(subject.getDetails().get("url")).isEqualTo(ENDPOINT_URI);
  }

  @Test
  void When_urlHealthIndicatorAndEndpointIsConnectedAndReturnsStatus2xx_Expect_StatusUp() {
    // arrange
    when(endpoint.getEndpointKey()).thenReturn(ENDPOINT_URI);

    // act
    Health subject = HttpHealthIndicators.urlHealthIndicator(endpoint);

    // assert
    assertThat(subject.getStatus()).isEqualTo(Status.UP);
    assertThat(subject.getDetails().get("url")).isEqualTo(ENDPOINT_URI);
  }
}
