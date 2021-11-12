package de.ikor.sip.foundation.security.authentication.x509;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import de.ikor.sip.foundation.security.config.SecurityConfigProperties;
import de.ikor.sip.foundation.security.config.SecurityConfigProperties.AuthProviderSettings;
import de.ikor.sip.foundation.security.config.SecurityConfigProperties.ValidationSettings;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class SIPX509FileValidatorTest {

  @Test
  void WHEN_ctor_WITH_badFileConfig_THEN_exception() throws Exception {
    // arrange
    AuthProviderSettings authProvSettings = new AuthProviderSettings();
    authProvSettings.setClassname(SIPX509AuthenticationProvider.class);
    authProvSettings.setValidation(
        new ValidationSettings(
            SIPX509FileValidator.class, new ClassPathResource("x509-test-bad.acl")));
    SecurityConfigProperties config = new SecurityConfigProperties();
    config.setAuthProviders(Collections.singletonList(authProvSettings));

    // act
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> new SIPX509FileValidator(config));

    // assert via exception
  }

  @Test
  void WHEN_isValid_WITH_validUser_THEN_true() throws Exception {
    // arrange
    AuthProviderSettings authProvSettings = new AuthProviderSettings();
    authProvSettings.setClassname(SIPX509AuthenticationProvider.class);
    authProvSettings.setValidation(
        new ValidationSettings(
            SIPX509FileValidator.class, new ClassPathResource("x509-test-good.acl")));
    SecurityConfigProperties config = new SecurityConfigProperties();
    config.setAuthProviders(Collections.singletonList(authProvSettings));

    SIPX509FileValidator subject = new SIPX509FileValidator(config);

    SIPX509AuthenticationToken authToken =
        new SIPX509AuthenticationToken(
            "CN=Full Name, EMAILADDRESS=name@domain.de, O=[*], C=DE", false);

    // act
    boolean result = subject.isValid(authToken);

    // assert
    assertThat(result)
        .withFailMessage("Auth token should have been successfully validated, but it failed")
        .isTrue();
  }

  @Test
  void WHEN_isValid_WITH_validUserWithWildcard_THEN_true() throws Exception {
    // arrange
    AuthProviderSettings authProvSettings = new AuthProviderSettings();
    authProvSettings.setClassname(SIPX509AuthenticationProvider.class);
    authProvSettings.setValidation(
        new ValidationSettings(
            SIPX509FileValidator.class, new ClassPathResource("x509-test-good.acl")));
    SecurityConfigProperties config = new SecurityConfigProperties();
    config.setAuthProviders(Collections.singletonList(authProvSettings));

    SIPX509FileValidator subject = new SIPX509FileValidator(config);

    SIPX509AuthenticationToken authToken =
        new SIPX509AuthenticationToken(
            "CN=Full Name, EMAILADDRESS=name@domain.de, O=whateverThisShouldBeIgnored, C=DE",
            false);

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
    authProvSettings.setClassname(SIPX509AuthenticationProvider.class);
    authProvSettings.setValidation(
        new ValidationSettings(
            SIPX509FileValidator.class, new ClassPathResource("x509-test-good.acl")));
    SecurityConfigProperties config = new SecurityConfigProperties();
    config.setAuthProviders(Collections.singletonList(authProvSettings));

    SIPX509FileValidator subject = new SIPX509FileValidator(config);

    SIPX509AuthenticationToken authToken =
        new SIPX509AuthenticationToken(
            "CN=Full Name, EMAILADDRESS=unknown@unknown.de, O=[*], C=DE", false);

    // act
    boolean result = subject.isValid(authToken);

    // assert
    assertThat(result)
        .withFailMessage("Auth token validation should have failed, but it succeeded")
        .isFalse();
  }
}
