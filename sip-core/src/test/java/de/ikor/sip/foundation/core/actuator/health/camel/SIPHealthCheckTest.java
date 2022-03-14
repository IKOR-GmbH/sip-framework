package de.ikor.sip.foundation.core.actuator.health.camel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.stream.Stream;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.ServiceStatus;
import org.apache.camel.health.HealthCheck;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SIPHealthCheckTest {

  private static final String TEST_ROUTE_NAME = "TestRoute";

  private CamelContext camelContext;
  private RouteController routeController;
  private Route route;

  @BeforeEach
  void setup() {
    camelContext = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    routeController = mock(RouteController.class);
    when(camelContext.getRouteController()).thenReturn(routeController);
    route = mock(Route.class, RETURNS_DEEP_STUBS);
    doReturn(TEST_ROUTE_NAME).when(route).getId();
    doReturn(TEST_ROUTE_NAME).when(route).getRouteId();

    when(route.getCamelContext()).thenReturn(camelContext);
    when(camelContext.getRoutes()).thenReturn(List.of(route));
  }

  @Test
  void Given_OneRoute_When_CreateRepository_Then_SIPHealthChecksReturned() {
    // arrange
    SIPHealthCheckRoutesRepository sipRoutesHealthCheckRepository =
        new SIPHealthCheckRoutesRepository();
    SIPHealthCheckConsumersRepository sipHealthCheckConsumersRepository =
        new SIPHealthCheckConsumersRepository();
    sipRoutesHealthCheckRepository.setCamelContext(camelContext);
    sipHealthCheckConsumersRepository.setCamelContext(camelContext);

    when(routeController.getRouteStatus(route.getId())).thenReturn(ServiceStatus.Suspended);

    // act
    Stream<HealthCheck> routeHealthChecks = sipRoutesHealthCheckRepository.stream();
    Stream<HealthCheck> consumerHealthChecks = sipHealthCheckConsumersRepository.stream();

    // assert
    assertThat(routeHealthChecks.toArray())
        .hasOnlyElementsOfType(SIPHealthCheckRoute.class)
        .hasSize(1);
    assertThat(consumerHealthChecks.toArray())
        .hasOnlyElementsOfType(SIPHealthCheckConsumer.class)
        .hasSize(1);
  }
}
