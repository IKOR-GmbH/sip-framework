package de.ikor.sip.foundation.core.registration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * A configuration class that provides properties specific to an adapter instance.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sip.core.backend-registration")
public class AdapterRegistrationProperties {

  /**
   * This is a unique id that identifies an instance of an adapter.
   * The value is generated and can not be overridden.
   */
  @Setter(AccessLevel.NONE)
  private final UUID instanceId = UUID.randomUUID();

  /**
   * The adapter name is equal for every adapter instance.
   * It is used to assign the adapter instances to an adapter type.
   * Maximum length of adapter name length is 64
   * <p>
   * This property is required
   */
  private String adapterName;

  /**
   * URL of the SIP Backend.
   * e.g. https://sip-backend.ikor.de/api/v1/client
   * <p>
   * This property is required
   */
  private String url;

  /**
   * The URI of this adapter instance.
   * e.g. http://127.0.0.1:8080
   * <p>
   * This property is optional
   */
  private String instanceUri;

  /**
   * The interval in which this adapter will send the telemetry data.
   * The value of register interval has to between 1000ms and 120000ms.
   * Default is 30000 milliseconds
   * <p>
   * This property is optional
   */
  private Long interval = 30000L;

  /**
   * Connection timeout of the web client.
   * The value of read timeout has to be between 100ms and 60000ms.
   * Default is 5000 milliseconds
   * <p>
   * This property is optional
   */
  private Long connectTimeout = 5000L;

  /**
   * Read timeout of the web client.
   * The value of read timeout has to be between 100ms and 60000ms.
   * Default is 5000 milliseconds
   * <p>
   * This property is optional
   */
  private Long readTimeout = 5000L;

  /**
   * The stage refers to the staging environment in which the application is executed.
   * This value is relevant to be able to differentiate between different adapter
   * instances in the SIP backend.
   * <p>
   * This property is optional
   */
  private String stage;
}
