package de.ikor.sip.foundation.security.authentication.apikey;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.ikor.sip.foundation.security.authentication.common.extractors.TokenExtractors;
import de.ikor.sip.foundation.security.authentication.common.validators.SIPAlwaysAllowValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SIPApiKeyAuthenticationProviderTest {

  @Test
  void WHEN_postConstruct_THEN_tokenExtractorAdded() {
    // arrange
    TokenExtractors extractors = mock(TokenExtractors.class);
    SIPApiKeyAuthenticationProvider subject =
        new SIPApiKeyAuthenticationProvider(extractors, new SIPAlwaysAllowValidator<>());

    // act
    ReflectionTestUtils.invokeMethod(subject, "postConstruct");

    // assert
    verify(extractors)
        .addMapping(eq(SIPApiKeyAuthenticationProvider.class), any(SIPApiKeyTokenExtractor.class));
  }
}
