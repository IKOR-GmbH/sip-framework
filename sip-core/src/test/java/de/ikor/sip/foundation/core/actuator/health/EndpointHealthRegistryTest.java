package de.ikor.sip.foundation.core.actuator.health;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Function;
import org.apache.camel.Endpoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

class EndpointHealthRegistryTest {

  private EndpointHealthRegistry subject;
  private Function<Endpoint, Health> healthFunction;
  private Endpoint endpoint;
  private Health health;
  private static final String ENDPOINT_URI = "test";
  private static final String ENDPOINT_ID = "testId";

  @BeforeEach
  void setUp() {
    subject = new EndpointHealthRegistry();
    health = Health.up().build();
    healthFunction = anEndpoint -> health;
    endpoint = mock(Endpoint.class);
  }

  @Test
  void
      When_registerNewHealthFunctionByEndpointUri_Expect_healthIndicatorIsPresentAndHealthMatches() {
    // arrange
    when(endpoint.getEndpointUri()).thenReturn(ENDPOINT_URI);

    // act
    subject.register(ENDPOINT_URI, healthFunction);
    Optional<EndpointHealthIndicator> healthIndicator = subject.healthIndicator(endpoint);

    // assert
    assertThat(healthIndicator).isNotNull().isPresent();
    assertThat(healthIndicator.get().health()).isEqualTo(health);
  }

  @Test
  void
      When_registerNewHealthFunctionByProcessorId_Expect_matchersByProcessorIdContainsEndpointIdAsKey() {
    // act
    subject.registerById(ENDPOINT_ID, healthFunction);

    // assert
    assertThat(subject.getMatchersByProcessorId().containsKey(ENDPOINT_ID)).isTrue();
    assertThat(subject.getMatchersByProcessorId().get(ENDPOINT_ID).contains(healthFunction))
        .isTrue();
  }

  @Test
  void When_healthIndicatorIsNotRegistered_Expect_notPresent() {
    // arrange
    when(endpoint.getEndpointUri()).thenReturn(ENDPOINT_URI);

    // act
    Optional<EndpointHealthIndicator> healthIndicator = subject.healthIndicator(endpoint);

    // assert
    assertThat(healthIndicator).isNotNull().isNotPresent();
  }
}
