package de.ikor.sip.foundation.security.authentication.apikey;

import de.ikor.sip.foundation.security.authentication.ConditionalOnSIPAuthProvider;
import de.ikor.sip.foundation.security.authentication.SIPAuthenticationProvider;
import de.ikor.sip.foundation.security.authentication.common.extractors.TokenExtractors;
import de.ikor.sip.foundation.security.authentication.common.validators.SIPTokenValidator;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * API Key authentication provider, wiring together the {@link SIPApiKeyTokenExtractor} and the
 * configured {@link SIPApiKeyTokenValidator}
 */
@ConditionalOnSIPAuthProvider(listItemValue = SIPApiKeyAuthenticationProvider.class)
@Component
public class SIPApiKeyAuthenticationProvider
    extends SIPAuthenticationProvider<SIPApiKeyAuthenticationToken> {

  private final TokenExtractors tokenExtractors;

  /**
   * Autowired constructor for creating the API Key authentication provider
   *
   * @param tokenExtractors the object to which the x509 token extractor should be added
   * @param tokenValidator the configured token validator
   */
  @Autowired
  public SIPApiKeyAuthenticationProvider(
      TokenExtractors tokenExtractors,
      SIPTokenValidator<SIPApiKeyAuthenticationToken> tokenValidator) {
    super(SIPApiKeyAuthenticationToken.class, tokenValidator);
    this.tokenExtractors = tokenExtractors;
  }

  @PostConstruct
  private void postConstruct() {
    tokenExtractors.addMapping(getClass(), new SIPApiKeyTokenExtractor());
  }
}
