package de.ikor.sip.foundation.security.authentication.apikey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class SIPApiKeyTokenExtractorTest {

  private SIPApiKeyTokenExtractor sipApiKeyTokenExtractor = new SIPApiKeyTokenExtractor();

  @Test
  void WHEN_extract_WITH_validApiKey_THEN_apiKeyExtracted() {
    // arrange
    String apiKey = UUID.randomUUID().toString();

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("x-api-key", apiKey);

    // act
    SIPApiKeyAuthenticationToken result = sipApiKeyTokenExtractor.extract(request);

    // assert
    assertThat(result.getPrincipal()).isEqualTo(apiKey);
  }

  @Test
  void WHEN_extract_WITH_absentApiKey_THEN_badCredentialsException() {
    // arrange
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("x-api-key-absent", "");

    // act + assert
    assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> sipApiKeyTokenExtractor.extract(request));
  }

  @Test
  void WHEN_getTokenType_THEN_returnAPIKeyType() {
    assertThat(sipApiKeyTokenExtractor.getTokenType())
        .isEqualTo(SIPApiKeyAuthenticationToken.class);
  }
}
