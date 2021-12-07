package de.ikor.sip.foundation.core.registration;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/** Allows POST and DELETE requests to be sent to an HTTP endpoint. */
@Slf4j
@AllArgsConstructor
class RegistrationWebClient {

  private final RestTemplate restTemplate;
  private final String baseUrl;

  public RegistrationWebClient(
      RestTemplate restTemplate, String baseUrl, Long connectTimeout, Long readTimeout) {
    Assert.isTrue(StringUtils.isNotBlank(baseUrl), "The base URL can not be blank.");
    Assert.isTrue(
        connectTimeout >= 100 && connectTimeout <= 60000,
        "The value of connect timeout has to be between 100ms and 60000ms.");
    Assert.isTrue(
        readTimeout >= 100 && readTimeout <= 60000,
        "The value of read timeout has to be between 100ms and 60000ms.");
    this.baseUrl = baseUrl;
    this.restTemplate =
        new RestTemplateBuilder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setConnectTimeout(Duration.ofMillis(connectTimeout))
            .setReadTimeout(Duration.ofMillis(readTimeout))
            .configure(restTemplate);
  }

  /**
   * Sends a POST request to provide the SIP Backend with telemetry data about an adapter instance.
   *
   * @param path of the registration endpoint (e.g. /register)
   * @param data contains detailed information about this adapter instance
   */
  public void sendPostRequest(String path, TelemetryData data) {
    String compositeUrl = baseUrl + path;
    log.debug("Send registration request {}", compositeUrl);
    try {
      this.restTemplate.exchange(
          compositeUrl, HttpMethod.POST, new HttpEntity<>(data), String.class);
    } catch (Exception exception) {
      log.warn(
          "Client registration request to {} failed due to {}",
          compositeUrl,
          exception.getMessage());
    }
  }

  /**
   * Sends a DELETE request to inform the SIP Backend that this adapter instance is shut down.
   *
   * @param path of the registration endpoint (e.g. /deregister)
   */
  public void sendDeleteRequest(String path) {
    String compositeUrl = baseUrl + path;
    log.debug("Send deregistration request {}", compositeUrl);
    try {
      this.restTemplate.exchange(compositeUrl, HttpMethod.DELETE, null, String.class);
    } catch (Exception exception) {
      log.warn(
          "Client deregistration request to {} failed due to {}",
          compositeUrl,
          exception.getMessage());
    }
  }
}
