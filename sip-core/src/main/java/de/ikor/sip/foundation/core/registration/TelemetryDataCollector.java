package de.ikor.sip.foundation.core.registration;

import de.ikor.sip.foundation.core.actuator.routes.AdapterRouteEndpoint;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Collects telemetry data of an adapter instance.
 */
@Slf4j
@AllArgsConstructor
class TelemetryDataCollector {

  private final TelemetryData telemetryData;
  private final AdapterRegistrationProperties properties;
  private final Environment environment;
  private final HealthEndpoint healthEndpoint;
  private final AdapterRouteEndpoint adapterRouteEndpoint;
  private final PathMappedEndpoints pathMappedEndpoints;

  /**
   * Initialize the necessary fields of this class.
   * Some of the properties of the provided object {@link TelemetryData} are set.
   * The scheme, address and port of this host are overridden in case they have not explicitly
   * been defined in the config file.
   *
   * @param instanceId       is a generated UUID
   * @param adapterName      the name of this adapter
   * @param instanceUri      the uri of this application with scheme, host and port
   * @param registerInterval interval in which this adapter instance is sending data to the backend
   */
  public void initialize(UUID instanceId, String adapterName, String instanceUri, Long registerInterval) {
    Assert.notNull(instanceId, "Instance ID name can not be null");
    Assert.isTrue(StringUtils.isNotBlank(adapterName) && adapterName.length() > 0 && adapterName.length() <= 64, "Maximum length of adapter name is 64 characters");
    Assert.notNull(registerInterval, "Register interval can not be null");
    this.telemetryData.setInstanceId(instanceId);
    this.telemetryData.setAdapterName(adapterName);
    this.telemetryData.setRegisterInterval(registerInterval);
    URI uri = determineInstanceUri(instanceUri);
    this.telemetryData.setInstanceUri(uri.toString());
    this.telemetryData.setInstanceScheme(uri.getScheme());
    this.telemetryData.setInstanceHost(uri.getHost());
    this.telemetryData.setInstancePort(uri.getPort());
  }

  /**
   * Collect current telemetry data of this application.
   *
   * @return the collected data as {@link TelemetryData}
   */
  public TelemetryData collectData() {
    Assert.notNull(this.telemetryData, "TelemetryData has not been initialized");
    this.telemetryData.setActuatorEndpoints(this.getAvailableActuatorEndpoints());
    this.telemetryData.setActiveProfiles(this.getActiveProfiles());
    this.telemetryData.setHealthStatus(this.getStatus());
    this.telemetryData.setAdapterRoutes(this.getAdapterRoutes());
    return this.telemetryData;
  }

  /**
   * Expects the instance uri as a parameter.
   * In case the uri is null or empty the instance uri
   * is constructed based on the configured scheme, host and port.
   *
   * @param instanceUri of this instance extracted from the config
   * @return the uri of this instance
   */
  private URI determineInstanceUri(String instanceUri) {
    try {
      if (StringUtils.isBlank(instanceUri)) {
        return new URI(String.format("%s://%s:%s", this.determineHostScheme(), this.determineHostAddress(), this.determineHostPort()));
      } else {
        return new URI(instanceUri);
      }
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(String.format("The instance uri %s was invalid", instanceUri));
    }
  }

  /**
   * The host address of this application can either configured or
   * it is automatically set using {@link InetAddress}. In case this
   * does not work 127.0.0.1 is used as default.
   *
   * @return host address of this application
   */
  private String determineHostAddress() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException unknownHostException) {
      log.warn("Instance host could not be determined fallback to 127.0.0.1");
    }
    return "127.0.0.1";
  }

  /**
   * The port of this application can either be configured or it is
   * extracted from the configuration file checking the value of server.port
   *
   * @return exposed port of this application
   */
  private Integer determineHostPort() {
    return environment.getProperty("server.port", Integer.class, 8080);
  }

  /**
   * The scheme of this application can either be configured or it is
   * extracted from the configuration file checking the value of server.ssl.enabled
   *
   * @return the configured scheme of this application
   */
  private String determineHostScheme() {
    boolean isSslEnabled = environment.getProperty("server.ssl.enabled", Boolean.class, false);
    return isSslEnabled ? "https" : "http";
  }

  private List<String> getAvailableActuatorEndpoints() {
    return new ArrayList<>(pathMappedEndpoints.getAllPaths());
  }

  /**
   * This retrieves the active profiles of this application that can be set in the configuration file.
   * They are used in order to determine what stage the application is running in (e.g. test, dev, prod)
   * In case active profiles are used for different purposes instead of the staging areas this value can be
   * overridden by using the config property sip.core.backend-registration.stage
   * (e.g. sip.core.backend-registration.stage=dev)
   *
   * @return a list containing all active profiles
   */
  private List<String> getActiveProfiles() {
    String stage = properties.getStage();
    return Objects.nonNull(stage) ? Collections.singletonList(stage) : Arrays.asList(environment.getActiveProfiles());
  }

  /**
   * This retrieves the health status via the HealthComponent of Spring.
   * The status is indicated by {@link Status#UP} and {@link Status#DOWN}.
   *
   * @return the composite health status of this application
   */
  private Status getStatus() {
    return this.healthEndpoint.health().getStatus();
  }

  /**
   * Get detailed adapter routes of this adapter instance.
   * The data of a route changes during runtime of the adapter for which reason this method is executed
   * for every registration process.
   *
   * @return a list containing all adapter route details
   */
  private List<?> getAdapterRoutes() {
    List<Object> routes = new ArrayList<>();
    this.adapterRouteEndpoint.routes().forEach(route -> routes.add(this.adapterRouteEndpoint.route(route.getId())));
    return routes;
  }
}