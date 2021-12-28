package de.ikor.sip.foundation.security.authentication.x509;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class SIPX509AuthenticationTokenTest {

  @Test
  void WHEN_ctor_WITH_nullPrincipal_THEN_exception() throws Exception {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> new SIPX509AuthenticationToken(null, true));
  }

  @Test
  void WHEN_withAuthenticated_WITH_true_THEN_sameObjectWithTrueAuthReturned() throws Exception {
    // arrange
    String expectedPrincipal = UUID.randomUUID().toString();

    SIPX509AuthenticationToken subject = new SIPX509AuthenticationToken(expectedPrincipal, false);

    // act
    SIPX509AuthenticationToken result = subject.withAuthenticated(true);

    // assert
    assertThat(result.getAuthorities()).isEmpty();
    assertThat(result.getCredentials()).isNull();
    assertThat(result.getDetails()).isNull();
    assertThat(result.getName()).isEqualTo(expectedPrincipal);
    assertThat(result.getPrincipal()).isEqualTo(expectedPrincipal);
    assertThat(result.isAuthenticated()).isTrue();
  }
}
