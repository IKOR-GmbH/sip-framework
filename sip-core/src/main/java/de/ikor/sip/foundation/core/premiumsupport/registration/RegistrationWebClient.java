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
class RegistrationWebClient {
  private final RestTemplate sipBackendRestTemplate;
  private final RegistrationConfigurationProperties properties;

  /**
   * Sends a POST request to provide the SIP Backend with telemetry data about an adapter instance.
   *
   * @param data contains detailed information about this adapter instance
   */
  public void sendPostRequest(TelemetryData data) {
    String path = "/register";
    String compositeUrl = properties.getUrl() + path;
    log.debug("Send registration request {}", compositeUrl);
    try {
      this.sipBackendRestTemplate.exchange(
          compositeUrl, HttpMethod.POST, new HttpEntity<>(data), String.class);
    } catch (Exception exception) {
      log.warn(
              //todo check if it is error or warn message
          "Client registration request to {} failed due to {}",
          compositeUrl,
          exception.getMessage());
    }
  }

  /**
   * Sends a DELETE request to inform the SIP Backend that this adapter instance is shut down.
   *
   */
  public void sendDeleteRequest(TelemetryData data) {
    String path = format("/deregister/%s", data.getInstanceId());
    String compositeUrl = properties.getUrl() + path;
    log.debug("Send deregistration request {}", compositeUrl);
    try {
      this.sipBackendRestTemplate.exchange(compositeUrl, HttpMethod.DELETE, null, String.class);
    } catch (Exception exception) {
      log.warn(
          "Client deregistration request to {} failed due to {}",
          compositeUrl,
          exception.getMessage());
    }
  }
}
