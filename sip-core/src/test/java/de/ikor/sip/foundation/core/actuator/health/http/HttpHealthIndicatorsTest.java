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

  private static final String ENDPOINT_URI = "https://www.google.com/";
  private static final String URL_KEY = "url";

  private Endpoint endpoint;

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
    assertThat(subject.getDetails()).containsEntry(URL_KEY, ENDPOINT_URI);
  }

  @Test
  void Given_EndpointIsConnectedAndReturnsStatus2xx_When_urlHealthIndicator_Then_StatusUp() {
    // arrange
    when(endpoint.getEndpointKey()).thenReturn(ENDPOINT_URI);

    // act
    Health subject = HttpHealthIndicators.urlHealthIndicator(endpoint);

    // assert
    assertThat(subject.getStatus()).isEqualTo(Status.UP);
    assertThat(subject.getDetails()).containsEntry(URL_KEY, ENDPOINT_URI);
  }
}
