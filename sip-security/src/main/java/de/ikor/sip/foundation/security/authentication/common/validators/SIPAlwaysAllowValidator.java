package de.ikor.sip.foundation.security.authentication.common.validators;

import de.ikor.sip.foundation.security.authentication.SIPAuthenticationToken;
import de.ikor.sip.foundation.security.config.ConditionalOnSIPSecurityAuthenticationEnabled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Dummy validator which does no checks, and allows each token to be authenticated.
 *
 * @author thomas.stieglmaier
 * @param <T> the token type to be validated
 */
@Slf4j
@Component
@ConditionalOnSIPSecurityAuthenticationEnabled
public class SIPAlwaysAllowValidator<T extends SIPAuthenticationToken<T>>
    implements SIPTokenValidator<T> {

  @Override
  public boolean isValid(T token) {
    log.warn("sip.security.alwaysvalid");
    return true;
  }
}
