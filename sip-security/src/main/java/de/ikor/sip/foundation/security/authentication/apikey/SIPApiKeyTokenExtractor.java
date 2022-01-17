package de.ikor.sip.foundation.security.authentication.apikey;

import de.ikor.sip.foundation.security.authentication.common.extractors.SIPTokenExtractor;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

/** Token extractor for getting the API Key */
public class SIPApiKeyTokenExtractor implements SIPTokenExtractor<SIPApiKeyAuthenticationToken> {

  @Override
  public Class<SIPApiKeyAuthenticationToken> getTokenType() {
    return SIPApiKeyAuthenticationToken.class;
  }

  @Override
  public SIPApiKeyAuthenticationToken extract(HttpServletRequest request) {
    String apiKey =
        Optional.ofNullable(request.getHeader("x-api-key"))
            .orElseThrow(() -> new BadCredentialsException("No x-api-key found in request"));
    return new SIPApiKeyAuthenticationToken(apiKey, false);
  }
}
