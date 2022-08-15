package de.ikor.sip.foundation.testkit.config;

import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.DefaultRouteInvoker;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl.FileRouteInvoker;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.Route;
import org.apache.camel.component.file.FileEndpoint;
import org.apache.camel.impl.engine.DefaultRouteController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CamelContextLifecycleHandlerTest {

  private static final String ROUTE_ID = "routeId";

  private CamelContextLifecycleHandler subject;
  private ExtendedCamelContext camelContext;
  private DefaultRouteController defaultRouteController;

  @BeforeEach
  void setup() {
    camelContext = mock(ExtendedCamelContext.class);
    defaultRouteController = mock(DefaultRouteController.class);
    List<Route> routes = new ArrayList<>();
    Route route = mock(Route.class);
    routes.add(route);
    FileEndpoint fileEndpoint = mock(FileEndpoint.class);

    List<RouteInvoker> routeInvokers = new ArrayList<>();
    FileRouteInvoker fileRouteInvoker = new FileRouteInvoker(camelContext);
    DefaultRouteInvoker defaultRouteInvoker = new DefaultRouteInvoker(camelContext);
    routeInvokers.add(fileRouteInvoker);
    routeInvokers.add(defaultRouteInvoker);
    subject = new CamelContextLifecycleHandler(routeInvokers);

    when(camelContext.getRoutes()).thenReturn(routes);
    when(route.getEndpoint()).thenReturn(fileEndpoint);
    when(route.getRouteId()).thenReturn(ROUTE_ID);
  }

  @Test
  void GIVEN_routeAndRouteInvokers_WHEN_afterApplicationStart_THEN_verifySuspendingRoute()
      throws Exception {
    // arrange
    when(camelContext.getRouteController()).thenReturn(defaultRouteController);

    // act
    subject.afterApplicationStart(camelContext);

    // assert
    verify(defaultRouteController, times(1)).suspendRoute(ROUTE_ID);
  }
}
