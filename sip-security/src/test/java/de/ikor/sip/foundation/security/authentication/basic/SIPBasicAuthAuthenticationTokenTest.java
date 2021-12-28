package de.ikor.sip.foundation.security.authentication.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SIPBasicAuthAuthenticationTokenTest {

  @Test
  void WHEN_ctor_WITH_nullPrincipal_THEN_exception() throws Exception {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> new SIPBasicAuthAuthenticationToken(null, "pw", true));
  }

  @Test
  void WHEN_ctor_WITH_nullCredential_THEN_exception() throws Exception {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> new SIPBasicAuthAuthenticationToken("user", null, true));
  }

  @Test
  void WHEN_withAuthenticated_WITH_true_THEN_sameObjectWithTrueAuthReturned() throws Exception {
    // arrange
    String expectedPrincipal = UUID.randomUUID().toString();
    String expectedCredential = UUID.randomUUID().toString();

    SIPBasicAuthAuthenticationToken subject =
        new SIPBasicAuthAuthenticationToken(expectedPrincipal, expectedCredential, false);

    // act
    SIPBasicAuthAuthenticationToken result = subject.withAuthenticated(true);

    // assert
    assertThat(result.getAuthorities()).isEmpty();
    assertThat(result.getCredentials()).isEqualTo(expectedCredential);
    assertThat(result.getDetails()).isNull();
    assertThat(result.getName()).isEqualTo(expectedPrincipal);
    assertThat(result.getPrincipal()).isEqualTo(expectedPrincipal);
    assertThat(result.isAuthenticated()).isTrue();
  }

  @Test
  void WHEN_setAuthenticated_THEN_unsupportedOperation() throws Exception {
    // arrange
    SIPBasicAuthAuthenticationToken subject =
        new SIPBasicAuthAuthenticationToken("name", "pw", false);

    // act + assert
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> subject.setAuthenticated(true));
  }
}
