package de.ikor.sip.foundation.security.authentication.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.ikor.sip.foundation.security.config.SecurityConfigProperties;
import de.ikor.sip.foundation.security.config.SecurityConfigProperties.AuthProviderSettings;
import de.ikor.sip.foundation.security.config.SecurityConfigProperties.ValidationSettings;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class SIPBasicAuthFileValidatorTest {

  @Test
  void WHEN_ctor_WITH_badFileConfig_THEN_exception() throws Exception {
    // arrange
    AuthProviderSettings authProvSettings = new AuthProviderSettings();
    authProvSettings.setClassname(SIPBasicAuthAuthenticationProvider.class);
    authProvSettings.setValidation(
        new ValidationSettings(
            SIPBasicAuthFileValidator.class, new ClassPathResource("basic-auth-users-bad.json")));
    SecurityConfigProperties config = new SecurityConfigProperties();
    config.setAuthProviders(Collections.singletonList(authProvSettings));

    // act
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> new SIPBasicAuthFileValidator(config));

    // assert via exception
  }

  @Test
  void WHEN_isValid_WITH_validUser_THEN_true() throws Exception {
    // arrange
    AuthProviderSettings authProvSettings = new AuthProviderSettings();
    authProvSettings.setClassname(SIPBasicAuthAuthenticationProvider.class);
    authProvSettings.setValidation(
        new ValidationSettings(
            SIPBasicAuthFileValidator.class, new ClassPathResource("basic-auth-users-good.json")));
    SecurityConfigProperties config = new SecurityConfigProperties();
    config.setAuthProviders(Collections.singletonList(authProvSettings));

    SIPBasicAuthFileValidator subject = new SIPBasicAuthFileValidator(config);

    SIPBasicAuthAuthenticationToken authToken =
        new SIPBasicAuthAuthenticationToken("user", "password", false);

    // act
    boolean result = subject.isValid(authToken);

    // assert
    assertThat(result)
        .withFailMessage("Auth token should have been successfully validated, but it failed")
        .isTrue();
  }

  @Test
  void WHEN_isValid_WITH_invalidUser_THEN_false() throws Exception {
    // arrange
    AuthProviderSettings authProvSettings = new AuthProviderSettings();
    authProvSettings.setClassname(SIPBasicAuthAuthenticationProvider.class);
    authProvSettings.setValidation(
        new ValidationSettings(
            SIPBasicAuthFileValidator.class, new ClassPathResource("basic-auth-users-good.json")));
    SecurityConfigProperties config = new SecurityConfigProperties();
    config.setAuthProviders(Collections.singletonList(authProvSettings));

    SIPBasicAuthFileValidator subject = new SIPBasicAuthFileValidator(config);

    SIPBasicAuthAuthenticationToken authToken =
        new SIPBasicAuthAuthenticationToken("baduser", "badpassword", false);

    // act
    boolean result = subject.isValid(authToken);

    // assert
    assertThat(result)
        .withFailMessage("Auth token validation should have failed, but it succeeded")
        .isFalse();
  }

  @Test
  void WHEN_isValid_WITH_invalidPassword_THEN_false() throws Exception {
    // arrange
    AuthProviderSettings authProvSettings = new AuthProviderSettings();
    authProvSettings.setClassname(SIPBasicAuthAuthenticationProvider.class);
    authProvSettings.setValidation(
        new ValidationSettings(
            SIPBasicAuthFileValidator.class, new ClassPathResource("basic-auth-users-good.json")));
    SecurityConfigProperties config = new SecurityConfigProperties();
    config.setAuthProviders(Collections.singletonList(authProvSettings));

    SIPBasicAuthFileValidator subject = new SIPBasicAuthFileValidator(config);

    SIPBasicAuthAuthenticationToken authToken =
        new SIPBasicAuthAuthenticationToken("user", "badpassword", false);

    // act
    boolean result = subject.isValid(authToken);

    // assert
    assertThat(result)
        .withFailMessage("Auth token validation should have failed, but it succeeded")
        .isFalse();
  }
}
