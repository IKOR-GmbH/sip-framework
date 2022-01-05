package de.ikor.sip.foundation.core.premiumsupport.registration;

import static java.lang.String.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
class RegistrationWebClient implements SIPRegistrationClient {
  private final RestTemplate sipBackendRestTemplate;
  private final SIPRegistrationProperties properties;
  private final SIPTelemetryDataCollector telemetryDataCollector;

  public RegistrationWebClient(
      TelemetryDataCollector telemetryDataCollector,
      RestTemplateBuilder restTemplateBuilder,
      SIPRegistrationProperties properties) {
    this.properties = properties;
    this.telemetryDataCollector = telemetryDataCollector;
    this.sipBackendRestTemplate = initRestTemplate(restTemplateBuilder);
  }

  @Override
  public void registerAdapter() {
    HttpEntity<TelemetryData> entity = new HttpEntity<>(telemetryDataCollector.collectData());
    sendRequestToBackend(properties.getCheckInUrl(), HttpMethod.POST, entity);
  }

  @Override
  public void unregisterAdapter() {
    String compositeUrl = format("%s/%s", properties.getCheckOutUrl(), properties.getInstanceId());
    sendRequestToBackend(compositeUrl, HttpMethod.DELETE, null);
  }

  private void sendRequestToBackend(String compositeUrl, HttpMethod method, HttpEntity<?> entity) {
    String requestType = method.equals(HttpMethod.DELETE) ? "de-registration" : "registration";
    log.debug("Send {} request {}", requestType, compositeUrl);
    try {
      this.sipBackendRestTemplate.exchange(compositeUrl, method, entity, String.class);
    } catch (Exception exception) {
      log.warn(
          "Client {} request to {} failed due to {}",
          requestType,
          compositeUrl,
          exception.getMessage());
    }
  }

  private RestTemplate initRestTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setConnectTimeout(properties.getConnectTimeout())
        .setReadTimeout(properties.getReadTimeout())
        .build();
  }
}
