package de.ikor.sip.foundation.core.premiumsupport.registration;

/**
 * Communicates with platform system. It sends heartbeat messages with telemetry data, or
 * un-registers the adapter from the platform.
 */
public interface SIPRegistrationClient {
  /**
   * Sends a POST request to provide the SIP Backend with telemetry data about an adapter instance.
   */
  void registerAdapter();

  /** Sends a DELETE request to inform the SIP Backend that this adapter instance is shut down. */
  void unregisterAdapter();
}
