package de.ikor.sip.foundation.core.translate;

/** Service used to get translated messages */
public interface SIPTranslateMessageService {
  /**
   * Get a translated message by providing its key
   *
   * @param key a key to which a message is bound
   * @return a translated message
   */
  String getTranslatedMessage(String key);

  /**
   * Get a translated message by providing its key and message arguments
   *
   * @param key a key to which a message is bound
   * @param args an array of arguments that will be filled in for params within the message
   * @return translated message
   */
  String getTranslatedMessage(String key, Object... args);
}
