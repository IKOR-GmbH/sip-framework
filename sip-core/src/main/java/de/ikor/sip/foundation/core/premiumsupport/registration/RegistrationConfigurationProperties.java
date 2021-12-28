package de.ikor.sip.foundation.core.premiumsupport.registration;

import java.time.Duration;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/** A configuration class that provides properties specific to an adapter instance. */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "sip.core.backend-registration")
@ComponentScan("de.ikor.sip.foundation.core.premiumsupport.registration")
public class RegistrationConfigurationProperties {

  /**
   * This is a unique id that identifies an instance of an adapter. The value is generated and can
   * not be overridden.
   */
  @Setter(AccessLevel.NONE)
  private final UUID instanceId = UUID.randomUUID();

  /**
   * The adapter name is equal for every adapter instance. It is used to assign the adapter
   * instances to an adapter type. Maximum length of adapter name length is 64
   *
   * <p>This property is required
   */
  @NotBlank()
  @Length(max = 64)
  private String adapterName;

  /**
   * URL of the SIP Backend. e.g. https://sip-backend.ikor.de/api/v1/client
   *
   * <p>This property is required
   */
  @NotBlank()
  private String url;

  /**
   * The interval in which this adapter will send the telemetry data. The value of register interval
   * has to between 1000ms and 120000ms. Default is 30000 milliseconds
   *
   * <p>This property is optional
   */
  private Long interval = 30000L;//TODO add test to check if default val is set

  /**
   * Connection timeout of the web client. The value of read timeout has to be between 100ms and
   * 60000ms. Default is 5000 milliseconds
   *
   * <p>This property is optional
   */
  @DurationMin(millis = 100)
  @DurationMax(millis = 6000)
  private Duration connectTimeout = Duration.ofMillis(5000);

  /**
   * Read timeout of the web client. The value of read timeout has to be between 100ms and 60000ms.
   * Default is 5000 milliseconds
   *
   * <p>This property is optional
   */
  @DurationMin(millis = 100)
  @DurationMax(millis = 6000)
  private Duration readTimeout = Duration.ofMillis(5000);

}
