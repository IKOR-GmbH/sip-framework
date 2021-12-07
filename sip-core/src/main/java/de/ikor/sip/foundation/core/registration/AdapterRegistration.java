package de.ikor.sip.foundation.core.registration;

import de.ikor.sip.foundation.core.actuator.routes.AdapterRouteEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

/**
 * This class handles the SIP Backend registration of an adapter instance.
 */
@Slf4j
class AdapterRegistration implements InitializingBean, DisposableBean {

  private final AdapterRegistrationProperties properties;
  private final TelemetryDataCollector telemetryDataCollector;
  private final RegistrationScheduler registrationScheduler;
  private final RegistrationWebClient registrationWebClient;

  public AdapterRegistration(AdapterRegistrationProperties properties, Environment environment, HealthEndpoint healthEndpoint, AdapterRouteEndpoint adapterRouteEndpoint, PathMappedEndpoints pathMappedEndpoints) {
    this.properties = properties;
    this.registrationWebClient = new RegistrationWebClient(new RestTemplate(), properties.getUrl(), properties.getConnectTimeout(), properties.getReadTimeout());
    this.registrationScheduler = new RegistrationScheduler(properties.getInterval());
    this.telemetryDataCollector = new TelemetryDataCollector(new TelemetryData(), properties, environment, healthEndpoint, adapterRouteEndpoint, pathMappedEndpoints);
  }

  /**
   * Is executed after the application has been started and its properties have been set.
   */
  @Override
  public void afterPropertiesSet() {
    log.info("Adapter registration for {} with id {} is starting", this.properties.getAdapterName(), this.properties.getInstanceId());
    this.telemetryDataCollector.initialize(this.properties.getInstanceId(), this.properties.getAdapterName(),
            this.properties.getInstanceUri(), this.properties.getInterval());
    this.registrationScheduler.startScheduler(() -> this.registrationWebClient.sendPostRequest("/register", this.telemetryDataCollector.collectData()));
  }

  /**
   * Is executed just before the application terminates.
   */
  @Override
  public void destroy() {
    log.info("Adapter registration for {} with id {} is stopping", this.properties.getAdapterName(), this.properties.getInstanceId());
    this.registrationScheduler.stopScheduler();
    this.registrationWebClient.sendDeleteRequest(String.format("/deregister/%s", this.properties.getInstanceId()));
  }

}
