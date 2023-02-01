package de.ikor.sip.foundation.security.authentication.common.extractors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import de.ikor.sip.foundation.security.authentication.basic.SIPBasicAuthAuthenticationToken;
import de.ikor.sip.foundation.security.authentication.basic.SIPBasicAuthTokenExtractor;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class TokenExtractorsTest {

  private final TokenExtractors subject = new TokenExtractors();

  @Test
  void WHEN_addMapping_WITH_newProvider_THEN_mappingAdded() throws Exception {
    // arrange
    SIPBasicAuthAuthenticationToken token =
        new SIPBasicAuthAuthenticationToken("user", "password", false);
    SIPBasicAuthTokenExtractor extractor = mock(SIPBasicAuthTokenExtractor.class);
    when(extractor.getTokenType()).thenCallRealMethod();
    when(extractor.extract(any())).thenReturn(token);
    Class<?> mappingKey = Object.class;

    // act
    subject.addMapping(mappingKey, extractor);

    // assert
    assertThat(subject.extractTokenFor(mappingKey, new MockHttpServletRequest())).isEqualTo(token);
  }

  @Test
  void WHEN_addMapping_WITH_existingProvider_THEN_illegalStateException() throws Exception {
    // arrange
    SIPBasicAuthTokenExtractor extractor = new SIPBasicAuthTokenExtractor();
    Class<?> mappingKey = Object.class;

    // act
    subject.addMapping(mappingKey, extractor);

    // assert
    assertThatExceptionOfType(SIPFrameworkException.class)
        .isThrownBy(() -> subject.addMapping(mappingKey, extractor))
        .withMessageContaining("Token extractor mapping for this provider exists already:");
  }

  @Test
  void WHEN_addMapping_WITH_extractorsWithSameTokenType_THEN_illegalStateException()
      throws Exception {
    // arrange
    SIPBasicAuthTokenExtractor extractor1 = new SIPBasicAuthTokenExtractor();
    Class<?> mappingKey1 = Object.class;
    // the {} force this to be an anonymous class with the same token type, so we can easily
    // mock what would be bad code in the real implementation
    SIPBasicAuthTokenExtractor extractor2 = new SIPBasicAuthTokenExtractor() {};
    Class<?> mappingKey2 = Objects.class;

    // act
    subject.addMapping(mappingKey1, extractor1);

    // assert
    assertThatExceptionOfType(SIPFrameworkException.class)
        .isThrownBy(() -> subject.addMapping(mappingKey2, extractor2))
        .withMessageContaining(
            "A token extractor mapping for an extractor with the same token type already exists");
  }

  @Test
  void WHEN_addMapping_WITH_newProviderWithSameExtractor_THEN_illegalStateException()
      throws Exception {
    // arrange
    SIPBasicAuthTokenExtractor extractor = new SIPBasicAuthTokenExtractor();
    Class<?> mappingKey1 = Object.class;
    Class<?> mappingKey2 = Objects.class;

    // act
    subject.addMapping(mappingKey1, extractor);

    // assert
    assertThatExceptionOfType(SIPFrameworkException.class)
        .isThrownBy(() -> subject.addMapping(mappingKey2, extractor))
        .withMessageContaining("Token extractor mapping for this extractor exists already:");
  }
}
