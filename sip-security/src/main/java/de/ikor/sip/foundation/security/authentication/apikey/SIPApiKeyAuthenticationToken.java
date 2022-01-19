package de.ikor.sip.foundation.security.authentication.apikey;

import de.ikor.sip.foundation.security.authentication.SIPAuthenticationToken;
import lombok.NonNull;

/** Container for the API Key */
public class SIPApiKeyAuthenticationToken
    extends SIPAuthenticationToken<SIPApiKeyAuthenticationToken> {

  private String apiKey;

  /**
   * Creates an API Key token
   *
   * @param apiKey the api key a string
   * @param isAuthenticated indicates of the token is authenticated or not
   */
  public SIPApiKeyAuthenticationToken(@NonNull String apiKey, boolean isAuthenticated) {
    super(isAuthenticated);
    this.apiKey = apiKey;
  }

  @Override
  public SIPApiKeyAuthenticationToken withAuthenticated(boolean authenticated) {
    return new SIPApiKeyAuthenticationToken(this.apiKey, authenticated);
  }

  @Override
  public Object getPrincipal() {
    return this.apiKey;
  }
}
