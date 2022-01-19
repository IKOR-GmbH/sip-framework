package de.ikor.sip.foundation.security.authentication.apikey;

import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.api.ApiKeyStrategy;
import de.ikor.sip.foundation.security.config.SecurityConfigProperties;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class SIPApiKeyTokenValidatorTest {

  @Test
  void WHEN_isValid_WITH_validApiKey_THEN_true() throws Exception {
    // arrange
    SecurityConfigProperties.AuthProviderSettings authProvSettings =
        new SecurityConfigProperties.AuthProviderSettings();
    authProvSettings.setClassname(SIPApiKeyAuthenticationProvider.class);
    authProvSettings.setValidation(
        new SecurityConfigProperties.ValidationSettings(
            SIPApiKeyTokenValidator.class, new ClassPathResource("api-key-good.key")));
    SecurityConfigProperties config = new SecurityConfigProperties();
    config.setAuthProviders(Collections.singletonList(authProvSettings));

    List<ApiKeyStrategy> apiKeyStrategies =
        Collections.singletonList(new SIPApiKeyStrategy(config));
    SIPApiKeyTokenValidator sipApiKeyTokenValidator = new SIPApiKeyTokenValidator(apiKeyStrategies);

    SIPApiKeyAuthenticationToken sipApiKeyAuthenticationToken =
        new SIPApiKeyAuthenticationToken("asd.ö123,05123#AASDsf/1235", false);

    // act
    boolean result = sipApiKeyTokenValidator.isValid(sipApiKeyAuthenticationToken);

    // assert
    assertThat(result)
        .withFailMessage("API Key should have been successfully validated, but it failed")
        .isTrue();
  }

  @Test
  void WHEN_isValid_WITH_invalidKey_THEN_false() throws Exception {
    // arrange
    SecurityConfigProperties.AuthProviderSettings authProvSettings =
        new SecurityConfigProperties.AuthProviderSettings();
    authProvSettings.setClassname(SIPApiKeyAuthenticationProvider.class);
    authProvSettings.setValidation(
        new SecurityConfigProperties.ValidationSettings(
            SIPApiKeyTokenValidator.class, new ClassPathResource("api-key-bad.key")));
    SecurityConfigProperties config = new SecurityConfigProperties();
    config.setAuthProviders(Collections.singletonList(authProvSettings));

    List<ApiKeyStrategy> apiKeyStrategies =
        Collections.singletonList(new SIPApiKeyStrategy(config));
    SIPApiKeyTokenValidator sipApiKeyTokenValidator = new SIPApiKeyTokenValidator(apiKeyStrategies);

    SIPApiKeyAuthenticationToken sipApiKeyAuthenticationToken =
        new SIPApiKeyAuthenticationToken("extracted.key.from.request", false);

    // act
    boolean result = sipApiKeyTokenValidator.isValid(sipApiKeyAuthenticationToken);

    // assert
    assertThat(result)
        .withFailMessage("Auth token validation should have failed, but it succeeded")
        .isFalse();
  }
}
