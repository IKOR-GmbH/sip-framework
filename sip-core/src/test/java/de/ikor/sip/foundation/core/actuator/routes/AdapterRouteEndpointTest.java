package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Route;
import org.apache.camel.api.management.ManagedCamelContext;
import org.apache.camel.api.management.mbean.ManagedRouteMBean;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class AdapterRouteEndpointTest {

  private static final String ROUTE_ID = "test";
  private static final String SIPMC_ROUTE_URI = "sipmc:test";

  private CamelContext context;
  private AdapterRouteEndpoint subject;
  private ManagedCamelContext managedCamelContext;
  private ManagedRouteMBean managedRoute;

  @BeforeEach
  void setUp() {
    context = mock(CamelContext.class);
    managedCamelContext = mock(ManagedCamelContext.class);
    subject = new AdapterRouteEndpoint(context);
  }

  @Test
  void When_GettingRoute_Expect_RouteDetails() throws Exception {
    // arrange
    mockRoutesInContext();

    // act
    AdapterRouteDetails adapterRouteDetails = subject.route(ROUTE_ID);

    // assert
    assertThat(adapterRouteDetails.getId()).isEqualTo(ROUTE_ID);
  }

  @Test
  void When_GettingRoutes_Expect_RoutesNotEmpty() throws Exception {
    // arrange
    mockManagedRoute();
    Route route = getMockedRoute();
    when(context.getRoutes()).thenReturn(Arrays.asList(route));
    ReflectionTestUtils.setField(subject, "mbeanContext", managedCamelContext);

    // assert
    assertThat(subject.routes()).isNotEmpty();
  }

  @Test
  void When_GettingSipmcRoutes_Expect_IsNotEmpty() throws Exception {
    // arrange
    mockRoutesInContext();

    // assert
    assertThat(subject.sipmcRoutes()).isNotEmpty();
  }

  @Test
  void When_ResettingSipmcRoutes_Expect_ManagedRouteResetCalled() throws Exception {
    // arrange
    mockRoutesInContext();

    // act
    subject.resetSipmcRoute();

    // assert
    verify(context, times(1)).getRoutes();
    verify(managedCamelContext, times(1)).getManagedRoute(ROUTE_ID);
    verify(managedRoute, times(1)).reset();
  }

  @Test
  void When_ExecutingInvalidOperationOnRoute_Expect_IncompatibleOperationExceptionThrown() {
    // assert
    assertThatThrownBy(() -> subject.execute("", "none"))
        .isInstanceOf(IncompatibleOperationException.class);
    assertThatThrownBy(() -> subject.execute("", "SUSPEND"))
        .isInstanceOf(IncompatibleOperationException.class);
  }

  @Test
  void When_ExecutingOperationsOnRoute_Expect_getRouteControllerCalledForEachOperation() {
    // arrange
    when(context.getRouteController()).thenReturn(mock(RouteController.class));

    // act
    subject.execute(ROUTE_ID, "stop");
    subject.execute(ROUTE_ID, "start");
    subject.execute(ROUTE_ID, "suspend");
    subject.execute(ROUTE_ID, "resume");

    // assert
    verify(context, times(4)).getRouteController();
  }

  @Test
  void When_ResumingAllRoutes_Expect_CamelContextToCall_getRoutes_And_getRouteController()
      throws Exception {
    // arrange
    mockRoutesInContext();
    when(context.getRouteController()).thenReturn(mock(RouteController.class));
    // act
    subject.resumeAll();

    // assert
    verify(context, times(1)).getRoutes();
    verify(context, times(1)).getRouteController();
  }

  @Test
  void When_SuspendingAllRoutes_Expect_CamelContextToCall_getRoutes_And_getRouteController()
      throws Exception {
    // arrange
    mockRoutesInContext();
    when(context.getRouteController()).thenReturn(mock(RouteController.class));

    // act
    subject.suspendAll();

    // assert
    verify(context, times(1)).getRoutes();
    verify(context, times(1)).getRouteController();
  }

  private Route getMockedRoute() throws Exception {
    Route route = mock(Route.class);
    when(route.getRouteId()).thenReturn(ROUTE_ID);
    return route;
  }

  private void mockManagedRoute() throws Exception {
    managedRoute = mock(ManagedRouteMBean.class);
    when(managedRoute.getRouteId()).thenReturn(ROUTE_ID);
    when(managedRoute.getState()).thenReturn("test");
    when(managedRoute.getExchangesTotal()).thenReturn((long) 0);
    when(managedRoute.getExchangesCompleted()).thenReturn((long) 0);
    when(managedRoute.getExchangesFailed()).thenReturn((long) 0);
    when(managedRoute.getExchangesInflight()).thenReturn((long) 0);
    when(managedCamelContext.getManagedRoute(anyString())).thenReturn(managedRoute);
    when(context.getExtension(any())).thenReturn(managedCamelContext);
  }

  private void mockRoutesInContext() throws Exception {
    mockManagedRoute();
    Route route = getMockedRoute();
    Endpoint mockEndpoint = mock(Endpoint.class);
    when(route.getEndpoint()).thenReturn(mockEndpoint);
    when(mockEndpoint.getEndpointUri()).thenReturn(SIPMC_ROUTE_URI);
    when(context.getRoutes()).thenReturn(Arrays.asList(route));
    ReflectionTestUtils.setField(subject, "mbeanContext", managedCamelContext);
  }
}
