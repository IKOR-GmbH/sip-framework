package de.ikor.sip.foundation.core.premiumsupport.registration;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/** A configuration class that provides properties specific to an adapter instance. */
@Data
@Slf4j
@Validated
@Configuration
@ConfigurationProperties(prefix = "sip.core.backend-registration")
public class SIPRegistrationProperties {

  @PostConstruct
  public void setCompositeProperties() {
    String urlWithSlash =
        this.platformUrl.endsWith("/") ? this.platformUrl : this.platformUrl + "/";
    this.checkOutUrl = urlWithSlash + this.checkOutPath;
    this.checkInUrl = urlWithSlash + this.checkInPath;
  }

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
  @NotBlank() private String platformUrl;

  /** Registration endpoint path. Default value is 'register'. */
  private String checkInPath = "register";

  /** De-registration endpoint path. Default value is 'deregister'. */
  private String checkOutPath = "deregister";

  /**
   * The URI of this adapter instance. e.g. http://127.0.0.1:8080
   *
   * <p>This property is optional
   */
  private URI instanceUri;

  /**
   * The interval in which this adapter will send the telemetry data. The value of register interval
   * has to between 1000ms and 120000ms. Default is 30000 milliseconds
   *
   * <p>This property is optional
   */
  @Min(1000)
  @Max(120000)
  private Long interval = 30000L;

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

  /**
   * The stage refers to the staging environment in which the application is executed. This value is
   * relevant to be able to differentiate between different adapter instances in the SIP backend.
   *
   * <p>This property is optional
   */
  private String stage;

  private String checkOutUrl;
  private String checkInUrl;
}
