package de.ikor.sip.foundation.testkit.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.testkit.exception.UnsuspendedRouteException;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.*;
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
  void GIVEN_routeWithSuspendingConsumer_WHEN_afterApplicationStart_THEN_verifySuspendingRoute()
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
      GIVEN_routeWithNoSuspendingConsumer_WHEN_afterApplicationStart_THEN_verifyNoMatchingConditionForSuspending()
          throws Exception {
    // arrange
    Consumer consumer = mock(Consumer.class);
    when(route.getConsumer()).thenReturn(consumer);
    when(camelContext.getRouteController()).thenReturn(defaultRouteController);

    // act
    subject.afterApplicationStart(camelContext);

    // assert
    verify(defaultRouteController, times(0)).suspendRoute(ROUTE_ID);
  }

  @Test
  void
      GIVEN_routeWithSuspendingException_WHEN_afterApplicationStart_THEN_expectUnsuspendedRouteException()
          throws Exception {
    // arrange
    FileConsumer fileConsumer = mock(FileConsumer.class);
    when(route.getConsumer()).thenReturn(fileConsumer);
    when(camelContext.getRouteController()).thenReturn(defaultRouteController);
    doThrow(new Exception()).when(defaultRouteController).suspendRoute(ROUTE_ID);

    // act & assert
    assertThrows(
        UnsuspendedRouteException.class, () -> subject.afterApplicationStart(camelContext));
  }
}
