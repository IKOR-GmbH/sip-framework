package de.ikor.sip.foundation.core.premiumsupport.registration;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.ikor.sip.foundation.core.actuator.routes.AdapterRouteSummary;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

/** POJO that is used to transmit adapter instance specific data to the SIP Backend. */
@Data
@Slf4j
@Validated
@Configuration
@ConfigurationProperties(prefix = "sip.core.backend-registration")
@JsonSerialize
public class TelemetryData {

  public TelemetryData(Environment environment) {
    setInstanceUri(null, environment);
    this.instanceScheme = determineHostScheme(environment);
    this.instanceHost = determineHostAddress();
    this.instancePort = determineHostPort(environment);
    setStage("", environment);
  }

  /**
   * This is a unique id that identifies an instance of an adapter. Its value is assigned in {@link
   * RegistrationConfigurationProperties}
   */
  @Setter(AccessLevel.NONE)
  private UUID instanceId = UUID.randomUUID();

  /**
   * Used protocol of this adapter instance. If ssl is enabled https is used otherwise http. It is
   * either set via the config or determined automatically. Default value is http. Its value is
   * assigned in {@link TelemetryDataCollector}
   */
  @Setter(AccessLevel.NONE)
  private String instanceScheme;

  /**
   * Host address of this adapter instance. It is either set via the config or determined
   * automatically. Default value is 127.0.0.1 Its value is assigned in {@link
   * TelemetryDataCollector}
   */
  @Setter(AccessLevel.NONE)
  private String instanceHost;

  /**
   * Port of this adapter instance It is either set via the config or determined automatically. Its
   * value is assigned in {@link TelemetryDataCollector} Default value is 8080
   */
  @Setter(AccessLevel.NONE)
  private Integer instancePort;

  /**
   * The instance uri is composed of {@link #instanceScheme}, {@link #instanceHost} and {@link
   * #instancePort} Its value is assigned in {@link TelemetryDataCollector}
   */
  private URI instanceUri;

  /**
   * The name of this adapter instance. This value must be defined in the configuration file.
   * Maximum length of adapter name is 64.
   */
  @NotBlank()
  @Length(max = 64)
  private String adapterName;

  /** The version of the SIP Framework. This value must be defined in the configuration file. */
  private String version = "1.0.0";

  /**
   * The interval the adpater registeres at the SIP Backend. The value of register interval has to
   * between 1000ms and 120000ms. Default value is 30000 milliseconds.
   */
  @Min(1000)
  @Max(120000) // TODO add test to check value range
  private Long interval = 30000L;

  /**
   * Active profiles that have ben set for this application. It is used to determine on which stage
   * this application is deployed. For example test, dev or prod. There can be multiple active
   * profiles for which reason this is a list.
   */
  private List<String> activeProfiles;

  /**
   * Contains all exposed actuator endpoints of this application. They are used in order to
   * determine what features this adapter instance is providing.
   */
  private Collection<String> actuatorEndpoints;

  /** Overall health status of this application. */
  private Status healthStatus;

  /** Detailed information of all adapter routes. */
  private List<AdapterRouteSummary> adapterRoutes;

  /**
   * The stage refers to the staging environment in which the application is executed. This value is
   * relevant to be able to differentiate between different adapter instances in the SIP backend.
   *
   * <p>This property is optional
   */
  private String stage;

  public void setInstanceUri(URI instanceUri) {
    setInstanceUri(instanceUri, null);
  }

  public void setInstanceUri(URI instanceUri, Environment environment) {
    this.instanceUri = determineInstanceUri(instanceUri, environment);
  }

  public String getInstanceHost() {
    return instanceUri.getHost();
  }

  public int getInstancePort() {
    return instanceUri.getPort();
  }

  public String getInstanceScheme() {
    return instanceUri.getScheme();
  }

  private URI determineInstanceUri(URI instanceUri, Environment environment) {
    try {
      if (instanceUri == null) {
        return new URI(
            String.format(
                "%s://%s:%s",
                this.determineHostScheme(environment),
                this.determineHostAddress(),
                this.determineHostPort(environment)));
      } else {
        return instanceUri;
      }
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(
          String.format("The instance uri %s was invalid", instanceUri));
    }
  }

  /**
   * The host address of this application can either configured or it is automatically set using
   * {@link InetAddress}. In case this does not work 127.0.0.1 is used as default.
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
   * The port of this application can either be configured or it is extracted from the configuration
   * file checking the value of server.port
   *
   * @return exposed port of this application
   */
  private Integer determineHostPort(Environment environment) {
    return environment.getProperty("server.port", Integer.class, 8080);
  }

  /**
   * The scheme of this application can either be configured or it is extracted from the
   * configuration file checking the value of server.ssl.enabled
   *
   * @return the configured scheme of this application
   */
  private String determineHostScheme(Environment environment) {
    boolean isSslEnabled = environment.getProperty("server.ssl.enabled", Boolean.class, false);
    return isSslEnabled ? "https" : "http";
  }

  /**
   * This retrieves the active profiles of this application that can be set in the configuration
   * file. They are used in order to determine what stage the application is running in (e.g. test,
   * dev, prod) In case active profiles are used for different purposes instead of the staging areas
   * this value can be overridden by using the config property sip.core.backend-registration.stage
   * (e.g. sip.core.backend-registration.stage=dev)
   *
   * @return a list containing all active profiles
   */
  private void setStage(String stage, Environment environment) {
    activeProfiles =
        Objects.nonNull(stage)
            ? Collections.singletonList(stage)
            : Arrays.asList(environment.getActiveProfiles());
  }
}
