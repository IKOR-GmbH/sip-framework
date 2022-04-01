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

  private static final String ENDPOINT_URI = "https://httpbin.org/";
  private static final String URL_KEY = "url";
  private static final String STATUS_CODE_KEY = "statusCode";

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
    when(endpoint.getEndpointKey()).thenReturn(ENDPOINT_URI);

    // act
    Health subject = HttpHealthIndicators.alwaysUnknown(endpoint);

    // assert
    assertThat(subject.getStatus()).isEqualTo(Status.UNKNOWN);
    assertThat(subject.getDetails()).containsEntry(URL_KEY, ENDPOINT_URI);
  }

  @Test
  void Given_EndpointIsConnectedAndReturnsStatus2xx_When_urlHealthIndicator_Then_StatusUp() {

    when(endpoint.getEndpointKey()).thenReturn(ENDPOINT_URI);

    // act
    Health subject = HttpHealthIndicators.urlHealthIndicator(endpoint);

    // assert
    assertThat(subject.getStatus()).isEqualTo(Status.UP);
    assertThat(subject.getDetails()).containsEntry(URL_KEY, ENDPOINT_URI);
  }

  @Test
  void Given_EndpointIsConnectedAndReturnsStatus405_When_urlHealthIndicator_Then_StatusUp() {
    String postEndpoint = ENDPOINT_URI + "post";
    when(endpoint.getEndpointKey()).thenReturn(postEndpoint);

    // act
    Health subject = HttpHealthIndicators.urlHealthIndicator(postEndpoint, 1000).apply(endpoint);

    // assert
    assertThat(subject.getStatus()).isEqualTo(Status.UP);
    assertThat(subject.getDetails()).containsEntry(URL_KEY, postEndpoint);
    assertThat(subject.getDetails()).containsEntry(STATUS_CODE_KEY, 405);
  }

  @Test
  void Given_EndpointIsConnectedAndReturnsStatus500_When_urlHealthIndicator_Then_StatusDown() {
    String postEndpoint = ENDPOINT_URI + "status/500";
    when(endpoint.getEndpointKey()).thenReturn(postEndpoint);

    // act
    Health subject = HttpHealthIndicators.urlHealthIndicator(endpoint);

    // assert
    assertThat(subject.getStatus()).isEqualTo(Status.DOWN);
    assertThat(subject.getDetails()).containsEntry(URL_KEY, postEndpoint);
    assertThat(subject.getDetails()).containsEntry(STATUS_CODE_KEY, 500);
  }

  @Test
  void Given_EndpointIsConnectedAndReturnsStatus401_When_urlHealthIndicator_Then_StatusUnknown() {
    String postEndpoint = ENDPOINT_URI + "status/401";
    when(endpoint.getEndpointKey()).thenReturn(postEndpoint);

    // act
    Health subject = HttpHealthIndicators.urlHealthIndicator(endpoint);

    // assert
    assertThat(subject.getStatus()).isEqualTo(Status.UNKNOWN);
    assertThat(subject.getDetails()).containsEntry(URL_KEY, postEndpoint);
    assertThat(subject.getDetails()).containsEntry(STATUS_CODE_KEY, 401);
  }
}
