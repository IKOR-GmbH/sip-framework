package de.ikor.sip.foundation.core.premiumsupport.registration;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

class TelemetryDataTest {
  private static final long DEFAULT_INTERVAL = 30000L;
  private static SIPRegistrationProperties properties;
  private static Environment environment;
  private static Validator validator;

  @BeforeAll
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    environment = mock(Environment.class);
    properties = mock(SIPRegistrationProperties.class);
    when(properties.getInterval()).thenReturn(DEFAULT_INTERVAL);
    when(environment.getProperty("server.ssl.enabled", Boolean.class, false)).thenReturn(false);
    when(environment.getActiveProfiles()).thenReturn(new String[] {"test"});
  }

  @Test
  void When_activeProfileIsNotInConfig_Expect_telemetryDataPicksItUpFromEnvironment() {
    // arrange
    when(properties.getStage()).thenReturn(null);
    // act
    TelemetryData telemetryData = new TelemetryData(properties, environment);
    // assert
    assertThat(telemetryData.getActiveProfiles()).contains(environment.getActiveProfiles());
  }

  @Test
  void When_activeProfileIsInConfig_Expect_telemetryDataPicksItUpFromEnvironment() {
    // arrange
    when(properties.getStage()).thenReturn("develop");
    // act
    TelemetryData telemetryData = new TelemetryData(properties, environment);
    // assert
    assertThat(telemetryData.getActiveProfiles())
        .isEqualTo(Collections.singletonList(properties.getStage()));
  }

  @Test
  void When_SSLisDisabled_Expect_instanceSchemeIsSetToHTTP() {
    // arrange
    when(environment.getProperty("server.ssl.enabled", Boolean.class, false)).thenReturn(false);
    // act
    TelemetryData telemetryData = new TelemetryData(properties, environment);
    // assert
    assertThat(telemetryData.getInstanceScheme()).isEqualTo("http");
  }

  @Test
  void When_sslIsEnabled_Expect_instanceSchemeIsSetToHTTPS() {
    // arrange
    when(environment.getProperty("server.ssl.enabled", Boolean.class, false)).thenReturn(true);
    // act
    TelemetryData telemetryData = new TelemetryData(properties, environment);
    // assert
    assertThat(telemetryData.getInstanceScheme()).isEqualTo("https");
  }

  @Test
  void When_telemetryDataIsCreated_Expect_instancePortIsSet() {
    // arrange
    when(environment.getProperty("server.port", Integer.class, 8080)).thenReturn(8080);
    // act
    TelemetryData telemetryData = new TelemetryData(properties, environment);
    // assert
    assertThat(telemetryData.getInstancePort()).isEqualTo(8080);
  }

  @Test
  void When_instanceURIisConfigured_Expect_uriIsTakenFromConfig() {
    // arrange
    when(properties.getInstanceUri()).thenReturn(URI.create("http://myhost:442"));
    // act
    TelemetryData telemetryData = new TelemetryData(properties, environment);
    // assert
    assertThat(telemetryData.getInstanceUri()).isEqualTo(properties.getInstanceUri());
  }

  @Test
  void When_instanceURIisNotConfigured_Expect_uriIsTakenFromEnvironment()
      throws UnknownHostException {
    // arrange
    when(environment.getProperty("server.port", Integer.class, 8080)).thenReturn(8080);
    // act
    TelemetryData telemetryData = new TelemetryData(properties, environment);
    // assert
    assertThat(telemetryData.getInstanceUri())
        .isEqualTo(
            URI.create(format("http://%s:8080", InetAddress.getLocalHost().getHostAddress())));
  }

  @Test
  void When_telemetryDataIsCreated_Expect_getIntervalReturnsDefaultValue() {
    // act
    TelemetryData telemetryData = new TelemetryData(properties, environment);
    // assert
    assertThat(telemetryData.getInterval()).isEqualTo(DEFAULT_INTERVAL);
  }

  @Test
  void When_configuredIntervalIsToShort_Expect_ValidationError() {
    SIPRegistrationProperties properties = new SIPRegistrationProperties();
    properties.setInterval(999L);
    Optional<ConstraintViolation<SIPRegistrationProperties>> interval =
        getValidationViolationForField(properties, "interval");
    assertThat(interval).isPresent();
  }

  @Test
  void When_configuredIntervalIsToLong_Expect_ValidationError() {
    SIPRegistrationProperties properties = new SIPRegistrationProperties();
    properties.setInterval(120001L);
    Optional<ConstraintViolation<SIPRegistrationProperties>> interval =
        getValidationViolationForField(properties, "interval");
    assertThat(interval).isPresent();
  }

  @Test
  void When_configuredAdapterNameIsToLong_Expect_ValidationError() {
    TelemetryData telemetryData = new TelemetryData(properties, environment);
    telemetryData.setAdapterName(
        "to long adapter name which definitely must cause validation error");
    Optional<ConstraintViolation<TelemetryData>> interval =
        getValidationViolationForField(telemetryData, "adapterName");
    assertThat(interval).isPresent();
  }

  private Optional<ConstraintViolation<TelemetryData>> getValidationViolationForField(
      TelemetryData telemetryData, String fieldName) {
    Set<ConstraintViolation<TelemetryData>> violations = validator.validate(telemetryData);
    return violations.stream()
        .filter(violation -> violation.getPropertyPath().toString().equals(fieldName))
        .findFirst();
  }

  private Optional<ConstraintViolation<SIPRegistrationProperties>> getValidationViolationForField(
      SIPRegistrationProperties registrationProperties, String fieldName) {
    Set<ConstraintViolation<SIPRegistrationProperties>> violations =
        validator.validate(registrationProperties);
    return violations.stream()
        .filter(violation -> violation.getPropertyPath().toString().equals(fieldName))
        .findFirst();
  }
}
