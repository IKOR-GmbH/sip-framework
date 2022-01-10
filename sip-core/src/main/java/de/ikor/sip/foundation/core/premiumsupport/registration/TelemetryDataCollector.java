package de.ikor.sip.foundation.core.premiumsupport.registration;

import de.ikor.sip.foundation.core.actuator.routes.AdapterRouteEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class TelemetryDataCollector implements SIPTelemetryDataCollector {

  private final TelemetryData telemetryData;
  private final AdapterRouteEndpoint adapterRouteEndpoint;
  private final PathMappedEndpoints pathMappedEndpoints;
  private final HealthEndpoint healthEndpoint;

  /**
   * Creates and populates {@link TelemetryData} from {@link SIPRegistrationProperties} and {@link
   * Environment}
   *
   * @param configProps - Static telemetry data source
   * @param adapterRouteEndpoint - Camel routes telemetry data source
   * @param pathMappedEndpoints - Web API telemetry data source
   * @param healthEndpoint - Health telemetry data source
   * @param environment - Server and application static data sources
   */
  public TelemetryDataCollector(
      SIPRegistrationProperties configProps,
      AdapterRouteEndpoint adapterRouteEndpoint,
      PathMappedEndpoints pathMappedEndpoints,
      HealthEndpoint healthEndpoint,
      Environment environment) {
    this.telemetryData = new TelemetryData(configProps, environment);
    this.adapterRouteEndpoint = adapterRouteEndpoint;
    this.pathMappedEndpoints = pathMappedEndpoints;
    this.healthEndpoint = healthEndpoint;
  }

  @Override
  public TelemetryData collectData() {
    telemetryData.setActuatorEndpoints(pathMappedEndpoints.getAllPaths());
    telemetryData.setHealthStatus(this.healthEndpoint.health().getStatus());
    telemetryData.setAdapterRoutes(this.adapterRouteEndpoint.routes());
    return telemetryData;
  }
}