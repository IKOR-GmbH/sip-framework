package de.ikor.sip.foundation.core.registration;

import lombok.Data;
import org.springframework.boot.actuate.health.Status;

import java.util.List;
import java.util.UUID;

/**
 * POJO that is used to transmit adapter instance specific data to the SIP Backend.
 */
@Data
class TelemetryData {

  /**
   * This is a unique id that identifies an instance of an adapter.
   * Its value is assigned in {@link AdapterRegistrationProperties}
   */
  private UUID instanceId;

  /**
   * Used protocol of this adapter instance.
   * If ssl is enabled https is used otherwise http.
   * It is either set via the config or determined automatically.
   * Default value is http.
   * Its value is assigned in {@link TelemetryDataCollector}
   */
  private String instanceScheme;

  /**
   * Host address of this adapter instance.
   * It is either set via the config or determined automatically.
   * Default value is 127.0.0.1
   * Its value is assigned in {@link TelemetryDataCollector}
   */
  private String instanceHost;

  /**
   * Port of this adapter instance
   * It is either set via the config or determined automatically.
   * Its value is assigned in {@link TelemetryDataCollector}
   * Default value is 8080
   */
  private Integer instancePort;

  /**
   * The instance uri is composed of {@link #instanceScheme}, {@link #instanceHost} and {@link #instancePort}
   * Its value is assigned in {@link TelemetryDataCollector}
   */
  private String instanceUri;

  /**
   * The name of this adapter instance.
   * This value must be defined in the configuration file.
   * Maximum length of adapter name is 64.
   */
  private String adapterName;

  /**
   * The version of the SIP Framework.
   * This value must be defined in the configuration file.
   */
  private String version = "1.0.0";

  /**
   * The interval the adpater registeres at the SIP Backend.
   * The value of register interval has to between 1000ms and 120000ms.
   * Default value is 30000 milliseconds.
   */
  private Long registerInterval;

  /**
   * Active profiles that have ben set for this application.
   * It is used to determine on which stage this application
   * is deployed. For example test, dev or prod.
   * There can be multiple active profiles for which reason this
   * is a list.
   */
  private List<String> activeProfiles;

  /**
   * Contains all exposed actuator endpoints of this application.
   * They are used in order to determine what features this adapter instance
   * is providing.
   */
  private List<String> actuatorEndpoints;

  /**
   * Overall health status of this application.
   */
  private Status healthStatus;

  /**
   * Detailed information of all adapter routes.
   */
  private List<?> adapterRoutes;
}
