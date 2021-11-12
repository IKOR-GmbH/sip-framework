package de.ikor.sip.foundation.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.ikor.sip.foundation.security.authentication.basic.SIPBasicAuthAuthenticationToken;
import de.ikor.sip.foundation.security.authentication.common.validators.SIPTokenValidator;
import de.ikor.sip.foundation.security.authentication.x509.SIPX509AuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class SIPAuthenticationProviderTest {

  @Mock private SIPTokenValidator<SIPBasicAuthAuthenticationToken> validator;

  @Test
  void WHEN_ctor_WITH_nullTokenType_THEN_exception() throws Exception {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () ->
                new SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken>(null, validator) {});
  }

  @Test
  void WHEN_ctor_WITH_nullValidator_THEN_exception() throws Exception {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(
            () ->
                new SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken>(
                    SIPBasicAuthAuthenticationToken.class, null) {});
  }

  @Test
  void WHEN_authenticate_WITH_validToken_THEN_authenticated() throws Exception {
    // arrange
    SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken> subject =
        new SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken>(
            SIPBasicAuthAuthenticationToken.class, validator) {};

    SIPBasicAuthAuthenticationToken token =
        new SIPBasicAuthAuthenticationToken("user", "pw", false);

    when(validator.isValid(token)).thenReturn(true);

    // act
    Authentication result = subject.authenticate(token);

    // assert
    assertThat(result.isAuthenticated()).isTrue();
  }

  @Test
  void WHEN_authenticate_WITH_badToken_THEN_badcredentialsException() throws Exception {
    // arrange
    ((Logger) LoggerFactory.getLogger(SIPAuthenticationProvider.class)).setLevel(Level.INFO);
    SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken> subject =
        new SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken>(
            SIPBasicAuthAuthenticationToken.class, validator) {};

    SIPBasicAuthAuthenticationToken token =
        new SIPBasicAuthAuthenticationToken("user", "pw", false);

    when(validator.isValid(token)).thenReturn(false);

    // act + assert
    assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> subject.authenticate(token));
  }

  @Test
  void WHEN_authenticate_WITH_badTokenWithDebugLoging_THEN_badcredentialsException()
      throws Exception {
    // arrange
    ((Logger) LoggerFactory.getLogger(SIPAuthenticationProvider.class)).setLevel(Level.DEBUG);
    SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken> subject =
        new SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken>(
            SIPBasicAuthAuthenticationToken.class, validator) {};

    SIPBasicAuthAuthenticationToken token =
        new SIPBasicAuthAuthenticationToken("user", "pw", false);

    when(validator.isValid(token)).thenReturn(false);

    // act + assert
    assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> subject.authenticate(token));
  }

  @Test
  void WHEN_supports_WITH_differentToken_THEN_false() throws Exception {
    // arrange
    SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken> subject =
        new SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken>(
            SIPBasicAuthAuthenticationToken.class, validator) {};

    // act
    boolean result = subject.supports(SIPX509AuthenticationToken.class);

    // assert
    assertThat(result).isFalse();
  }
}
