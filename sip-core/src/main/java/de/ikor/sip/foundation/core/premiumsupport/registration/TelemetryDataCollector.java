package de.ikor.sip.foundation.core.premiumsupport.registration;

import de.ikor.sip.foundation.core.actuator.routes.AdapterRouteDetails;
import de.ikor.sip.foundation.core.actuator.routes.AdapterRouteEndpoint;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
class TelemetryDataCollector implements SIPTelemetryDataCollector {

  private static final String SIP_FRAMEWORK_VERSION = "sipFrameworkVersion";

  private final TelemetryData telemetryData;
  private final AdapterRouteEndpoint adapterRouteEndpoint;
  private final PathMappedEndpoints pathMappedEndpoints;
  private final HealthEndpoint healthEndpoint;
  private final BuildProperties buildProperties;

  /**
   * Creates and populates {@link TelemetryData} from {@link SIPRegistrationProperties} and {@link
   * Environment}
   *
   * @param configProps - Static telemetry data source
   * @param adapterRouteEndpoint - Camel routes telemetry data source
   * @param pathMappedEndpoints - Web API telemetry data source
   * @param healthEndpoint - Health telemetry data source
   * @param environment - Server and application static data sources
   * @param buildProperties - Adapter build info data
   */
  public TelemetryDataCollector(
      SIPRegistrationProperties configProps,
      AdapterRouteEndpoint adapterRouteEndpoint,
      PathMappedEndpoints pathMappedEndpoints,
      HealthEndpoint healthEndpoint,
      Environment environment,
      BuildProperties buildProperties) {
    this.telemetryData = new TelemetryData(configProps, environment);
    this.adapterRouteEndpoint = adapterRouteEndpoint;
    this.pathMappedEndpoints = pathMappedEndpoints;
    this.healthEndpoint = healthEndpoint;
    this.buildProperties = buildProperties;
  }

  @Override
  public TelemetryData collectData() {
    telemetryData.setActuatorEndpoints(pathMappedEndpoints.getAllPaths());
    telemetryData.setHealthStatus(this.healthEndpoint.health().getStatus());
    telemetryData.setAdapterName(this.buildProperties.getName());
    telemetryData.setAdapterVersion(this.buildProperties.getVersion());
    telemetryData.setSipFrameworkVersion(this.buildProperties.get(SIP_FRAMEWORK_VERSION));
    telemetryData.setAdapterRoutes(getAdapterRoutes());
    return telemetryData;
  }

  private List<AdapterRouteDetails> getAdapterRoutes() {
    return this.adapterRouteEndpoint.routes().stream()
        .map(route -> this.adapterRouteEndpoint.route(route.getId()))
        .collect(Collectors.toList());
  }
}
