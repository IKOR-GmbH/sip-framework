package de.ikor.sip.foundation.core.translate;

import java.util.List;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Config properties for defining all parameters important for translate service and message source
 */
@Data
@Configuration
@ComponentScan
@ConfigurationProperties(prefix = "sip.core.translation")
@ConditionalOnProperty(value = "sip.core.translation.enabled", havingValue = "true")
public class TranslateConfiguration {
  private List<String> fileLocations;
  private String lang = "en";
  private String defaultEncoding = "UTF-8";
  private Boolean fallbackToSystemLocale = false;
  private Boolean useCodeAsDefaultMessage = true;

  /**
   * Defines and configures a {@link MessageSource}
   *
   * @return {@link MessageSource}
   */
  @Bean
  @Primary
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
    for (String baseName : this.getFileLocations()) {
      messageSource.addBasenames(baseName);
    }
    messageSource.setDefaultEncoding(this.getDefaultEncoding());
    messageSource.setFallbackToSystemLocale(this.getFallbackToSystemLocale());
    messageSource.setUseCodeAsDefaultMessage(this.getUseCodeAsDefaultMessage());
    return messageSource;
  }
}
