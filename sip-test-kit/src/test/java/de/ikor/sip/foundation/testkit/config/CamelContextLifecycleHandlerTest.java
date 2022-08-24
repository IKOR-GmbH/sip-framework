package de.ikor.sip.foundation.testkit.config;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.Route;
import org.apache.camel.component.file.FileConsumer;
import org.apache.camel.impl.engine.DefaultRouteController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CamelContextLifecycleHandlerTest {

  private static final String ROUTE_ID = "routeId";

  private CamelContextLifecycleHandler subject;
  private CamelContext camelContext;
  private DefaultRouteController defaultRouteController;
  private Route route;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
    defaultRouteController = mock(DefaultRouteController.class);
    List<Route> routes = new ArrayList<>();
    route = mock(Route.class);
    routes.add(route);

    subject = new CamelContextLifecycleHandler();

    when(camelContext.getRoutes()).thenReturn(routes);
    when(route.getRouteId()).thenReturn(ROUTE_ID);
  }

  @Test
  void GIVEN_routeAndRouteInvokers_WHEN_afterApplicationStart_THEN_verifySuspendingRoute()
      throws Exception {
    // arrange
    FileConsumer fileConsumer = mock(FileConsumer.class);
    when(route.getConsumer()).thenReturn(fileConsumer);
    when(camelContext.getRouteController()).thenReturn(defaultRouteController);

    // act
    subject.afterApplicationStart(camelContext);

    // assert
    verify(defaultRouteController, times(1)).suspendRoute(ROUTE_ID);
  }

  @Test
  void
      GIVEN_routeWithDifferentEndpoint_WHEN_afterApplicationStart_THEN_verifyNoMatchingConditionForSuspending()
          throws Exception {
    // arrange
    Endpoint endpoint = mock(Endpoint.class);
    when(route.getEndpoint()).thenReturn(endpoint);
    when(camelContext.getRouteController()).thenReturn(defaultRouteController);

    // act
    subject.afterApplicationStart(camelContext);

    // assert
    verify(defaultRouteController, times(0)).suspendRoute(ROUTE_ID);
  }
}
