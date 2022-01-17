package de.ikor.sip.foundation.security.authentication.apikey;

import de.ikor.sip.foundation.core.api.ApiKeyStrategy;
import de.ikor.sip.foundation.security.config.SecurityConfigProperties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnBean(SIPApiKeyTokenValidator.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class SIPApiKeyStrategy implements ApiKeyStrategy {

  private String apiKey;

  @Override
  public String getApiKey() {
    return this.apiKey;
  }

  /**
   * Creates an API Key token validator and sets the API Key
   *
   * @param securityConfigProperties the config containing information on where to find the file
   *     that should be read
   */
  @Autowired
  public SIPApiKeyStrategy(SecurityConfigProperties securityConfigProperties) throws IOException {
    Resource apiKeyResource =
        securityConfigProperties
            .getSettingsForProvider(SIPApiKeyAuthenticationProvider.class)
            .getValidation()
            .getFilePath();
    if (Objects.nonNull(apiKeyResource)) {
      this.apiKey = this.readApiKeyFromFile(apiKeyResource.getFile());
    } else {
      this.apiKey = this.generateApiKey();
      log.debug("Generated API Key: {}", this.getApiKey());
    }
  }

  /**
   * Generates a new API Key in case the API Key is not read from a file.
   *
   * @return the generated API Key
   */
  private String generateApiKey() {
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(256);
      SecretKey secretKey = keyGenerator.generateKey();
      return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new SecurityException(
          "API Key could not be generated due to {}", noSuchAlgorithmException);
    }
  }

  /**
   * Read the API Key from a file which path can be configured in the application.yaml file.
   *
   * @param file pointing to the API Key file.
   * @return the content of the file
   */
  private String readApiKeyFromFile(File file) {
    try {
      try (Stream<String> lines = Files.lines(Paths.get(file.getPath()))) {
        return lines.collect(Collectors.joining(System.lineSeparator()));
      }
    } catch (Exception e) {
      throw new BadCredentialsException("API key could not be read due to " + e.getMessage());
    }
  }
}
