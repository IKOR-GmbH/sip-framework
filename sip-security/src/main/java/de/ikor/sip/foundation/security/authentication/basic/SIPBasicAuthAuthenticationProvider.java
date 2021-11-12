package de.ikor.sip.foundation.security.authentication.basic;

import de.ikor.sip.foundation.security.authentication.ConditionalOnSIPAuthProvider;
import de.ikor.sip.foundation.security.authentication.SIPAuthenticationProvider;
import de.ikor.sip.foundation.security.authentication.common.extractors.TokenExtractors;
import de.ikor.sip.foundation.security.authentication.common.validators.SIPTokenValidator;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Basic Authentication provider, wiring together the {@link SIPBasicAuthTokenExtractor} and the
 * configured {@link SIPTokenValidator}
 *
 * @author thomas.stieglmaier
 */
@ConditionalOnSIPAuthProvider(listItemValue = SIPBasicAuthAuthenticationProvider.class)
@Component
public class SIPBasicAuthAuthenticationProvider
    extends SIPAuthenticationProvider<SIPBasicAuthAuthenticationToken> {

  private TokenExtractors tokenExtractors;

  /**
   * Autowired constructor for creating the basic authentication provider
   *
   * @param tokenExtractors the object to which the basic auth token extractor should be added
   * @param tokenValidator the configured token validator
   */
  @Autowired
  public SIPBasicAuthAuthenticationProvider(
      TokenExtractors tokenExtractors,
      SIPTokenValidator<SIPBasicAuthAuthenticationToken> tokenValidator) {
    super(SIPBasicAuthAuthenticationToken.class, tokenValidator);
    this.tokenExtractors = tokenExtractors;
  }

  @PostConstruct
  private void postConstruct() {
    tokenExtractors.addMapping(getClass(), new SIPBasicAuthTokenExtractor());
  }
}
