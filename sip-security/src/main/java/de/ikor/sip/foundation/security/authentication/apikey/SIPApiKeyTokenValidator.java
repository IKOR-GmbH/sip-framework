package de.ikor.sip.foundation.security.authentication.apikey;

import de.ikor.sip.foundation.core.api.ApiKeyStrategy;
import de.ikor.sip.foundation.security.authentication.common.validators.SIPTokenValidator;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

/**
 * The validator is verifying if an API Key is not blank nor exceeding the allowed length of 255
 * characters And then validates the extracted API Token from the request with the generated API Key
 * or API Key from a file.
 */
@ConditionalOnBean(SIPApiKeyTokenValidator.class)
@Component
@Slf4j
public class SIPApiKeyTokenValidator implements SIPTokenValidator<SIPApiKeyAuthenticationToken> {

  private final List<ApiKeyStrategy> apiKeyStrategies;

  public SIPApiKeyTokenValidator(List<ApiKeyStrategy> apiKeyStrategies) {
    this.apiKeyStrategies = new ArrayList<>(apiKeyStrategies);
  }

  @Override
  public boolean isValid(SIPApiKeyAuthenticationToken token) {
    String extractedApiKey = token.getPrincipal().toString();
    ApiKeyStrategy apiKeyStrategy =
        this.apiKeyStrategies.stream()
            .findFirst()
            .orElseThrow(
                () -> new BadCredentialsException("No suitable APIKeyStrategy could be found"));
    return StringUtils.isNotBlank(extractedApiKey)
        && extractedApiKey.length() <= 255
        && apiKeyStrategy.getApiKey().equals(extractedApiKey);
  }
}
