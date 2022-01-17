package de.ikor.sip.foundation.core.api;

/** A strategy that handles API keys */
public interface ApiKeyStrategy {

  /**
   * Provides an API key that could be used for authentication
   *
   * @return an API key as a string
   */
  String getApiKey();
}
