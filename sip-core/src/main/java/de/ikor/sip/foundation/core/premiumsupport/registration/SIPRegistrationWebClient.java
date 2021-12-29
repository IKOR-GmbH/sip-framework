package de.ikor.sip.foundation.core.premiumsupport.registration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;

/** Allows POST and DELETE requests to be sent to an HTTP endpoint. */
@Slf4j
@Service
@AllArgsConstructor
class SIPRegistrationWebClient {
  private final RestTemplate sipBackendRestTemplate;
  private final SIPRegistrationProperties properties;
  private final TelemetryDataCollector telemetryDataCollector;

  /**
   * Sends a POST request to provide the SIP Backend with telemetry data about an adapter instance.
   */
  public void registerAdapter() {
    String compositeUrl = properties.getUrl() + properties.getRegistrationPath();
    HttpEntity<TelemetryData> entity = new HttpEntity<>(telemetryDataCollector.collectData());
    log.debug("Send registration request {}", compositeUrl);
    sendRequestToBackend(compositeUrl, HttpMethod.POST, entity);
  }

  /** Sends a DELETE request to inform the SIP Backend that this adapter instance is shut down. */
  public void unregisterAdapter() {
    String path = format("%s/%s", properties.getDeregistrationPath(), properties.getInstanceId());
    String compositeUrl = properties.getUrl() + path;
    log.debug("Send de-registration request {}", compositeUrl);
    sendRequestToBackend(compositeUrl, HttpMethod.DELETE, null);
  }

  private void sendRequestToBackend(String compositeUrl, HttpMethod method, HttpEntity<?> entity) {
    try {
      this.sipBackendRestTemplate.exchange(compositeUrl, method, entity, String.class);
    } catch (Exception exception) {
      String requestType = method.equals(HttpMethod.DELETE) ? "de-registration" : "registration";
      log.warn(
          "Client {} request to {} failed due to {}",
          requestType,
          compositeUrl,
          exception.getMessage());
    }
  }
}
