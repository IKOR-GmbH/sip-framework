package de.ikor.sip.foundation.core.translate;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/** Implementation of {@link SIPTranslateMessageService} */
@Service
public class SIPTranslateMessageServiceImpl implements SIPTranslateMessageService {
  private final Locale language;
  private final MessageSource messageSource;

  /**
   * Creates new instance of {@link SIPTranslateMessageServiceImpl}
   *
   * @param messageSource {@link MessageSource}
   * @param translateConfiguration {@link TranslateConfiguration}
   */
  @Autowired
  public SIPTranslateMessageServiceImpl(
      MessageSource messageSource, TranslateConfiguration translateConfiguration) {
    this.messageSource = messageSource;
    this.language = new Locale(translateConfiguration.getLang());
  }

  @Override
  public String getTranslatedMessage(String key) {
    return messageSource.getMessage(key, null, language);
  }

  @Override
  public String getTranslatedMessage(String key, Object... args) {
    return messageSource.getMessage(key, args, language);
  }
}
