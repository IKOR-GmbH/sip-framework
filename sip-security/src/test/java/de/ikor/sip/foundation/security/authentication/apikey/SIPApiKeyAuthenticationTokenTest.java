package de.ikor.sip.foundation.security.authentication.apikey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SIPApiKeyAuthenticationTokenTest {

  @Test
  void WHEN_constructor_WITH_nullPrincipal_THEN_exception() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> new SIPApiKeyAuthenticationToken(null, true));
  }

  @Test
  void WHEN_constructor_WITH_validParams_THEN_correctValueslReturned() {
    // arrange
    String expectedPrincipal = UUID.randomUUID().toString();
    boolean expectedAuth = false;

    // act
    SIPApiKeyAuthenticationToken subject =
        new SIPApiKeyAuthenticationToken(expectedPrincipal, expectedAuth);

    // assert
    assertThat(subject.getAuthorities()).isEmpty();
    assertThat(subject.getCredentials()).isNull();
    assertThat(subject.getDetails()).isNull();
    assertThat(subject.getName()).isEqualTo(expectedPrincipal);
    assertThat(subject.getPrincipal()).isEqualTo(expectedPrincipal);
    assertThat(subject.isAuthenticated()).isEqualTo(expectedAuth);
  }

  @Test
  void WHEN_withAuthenticated_WITH_true_THEN_sameObjectWithTrueAuthReturned() {
    // arrange
    String expectedPrincipal = UUID.randomUUID().toString();

    SIPApiKeyAuthenticationToken subject =
        new SIPApiKeyAuthenticationToken(expectedPrincipal, false);

    // act
    SIPApiKeyAuthenticationToken result = subject.withAuthenticated(true);

    // assert
    assertThat(result.getAuthorities()).isEmpty();
    assertThat(result.getCredentials()).isNull();
    assertThat(result.getDetails()).isNull();
    assertThat(result.getName()).isEqualTo(expectedPrincipal);
    assertThat(result.getPrincipal()).isEqualTo(expectedPrincipal);
    assertThat(result.isAuthenticated()).isTrue();
  }
}
