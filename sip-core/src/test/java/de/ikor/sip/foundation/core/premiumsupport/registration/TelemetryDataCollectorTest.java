package de.ikor.sip.foundation.core.premiumsupport.registration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import de.ikor.sip.foundation.core.actuator.routes.AdapterRouteEndpoint;
import de.ikor.sip.foundation.core.actuator.routes.AdapterRouteSummary;
import java.util.*;
import org.apache.camel.api.management.mbean.ManagedRouteMBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.core.env.Environment;

class TelemetryDataCollectorTest {

  @Mock SIPRegistrationProperties properties;
  @Mock private Environment environment;
  @Mock private HealthEndpoint healthEndpoint;
  @Mock private AdapterRouteEndpoint adapterRouteEndpoint;
  @Mock private PathMappedEndpoints pathMappedEndpoints;
  @Mock private HealthComponent healthComponent;
  @Mock private ManagedRouteMBean managedRouteMBean;
  private TelemetryDataCollector telemetryDataCollector;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);

    when(healthEndpoint.health()).thenReturn(healthComponent);
    when(healthEndpoint.health().getStatus()).thenReturn(Status.UP);

    List<AdapterRouteSummary> list = new ArrayList<>();
    list.add(new AdapterRouteSummary(managedRouteMBean));
    when(adapterRouteEndpoint.routes()).thenReturn(list);

    when(environment.getActiveProfiles()).thenReturn(new String[] {"test"});
    when(environment.getProperty("server.ssl.enabled", Boolean.class, false)).thenReturn(false);
    this.telemetryDataCollector =
        new TelemetryDataCollector(
            properties, adapterRouteEndpoint, pathMappedEndpoints, healthEndpoint, environment);
  }

  @Test
  void
      when_telemetryDataCollectorIsCalled_Expect_ActuatorEndpointListReturnedFromPathMappedEndpoints() {
    // arrange
    when(pathMappedEndpoints.getAllPaths()).thenReturn(Arrays.asList("health", "metrics"));
    // act
    TelemetryData telemetryData = telemetryDataCollector.collectData();
    // assert
    assertThat(pathMappedEndpoints.getAllPaths()).isEqualTo(telemetryData.getActuatorEndpoints());
  }

  @Test
  void when_telemetryDataCollectorIsCalled_Expect_CamelRoutesReturnedFromAdapterRouteEndpoint() {
    // arrange
    List<AdapterRouteSummary> routes = new LinkedList<>();
    routes.add(new AdapterRouteSummary(managedRouteMBean));
    when(adapterRouteEndpoint.routes()).thenReturn(routes);
    // act
    TelemetryData telemetryData = telemetryDataCollector.collectData();
    // assert
    assertThat(adapterRouteEndpoint.routes()).isEqualTo(telemetryData.getAdapterRoutes());
  }

  @Test
  void when_telemetryDataCollectorIsCalled_Expect_HealthStatusFromHealthEndpoint() {
    // arrange
    when(healthEndpoint.health().getStatus()).thenReturn(Status.DOWN);
    // act
    TelemetryData telemetryData = telemetryDataCollector.collectData();
    // assert
    assertThat(healthEndpoint.health().getStatus()).isEqualTo(telemetryData.getHealthStatus());
  }
}
