package de.ikor.sip.foundation.core.proxies.extension;

import org.apache.camel.Exchange;

/** Proxy extension API */
public interface ProxyExtension {

  /**
   * Method every extention should implement. Called when extension detected
   *
   * @param original Original Exchange
   * @param current Currently modified Exchange
   */
  void run(Exchange original, Exchange current);

  /**
   * Check whether this ProxyExtension should be used
   *
   * @param original {@link Exchange} before processing
   * @param current {@link Exchange} after processing
   * @return true if this ProxyExtension is applicable
   */
  boolean isApplicable(Exchange original, Exchange current);
}