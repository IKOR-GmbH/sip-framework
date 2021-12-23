package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.ikor.sip.foundation.core.actuator.common.IntegrationManagementException;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteOperationTest {

  public static final String ROUTE_ID = "id";

  private CamelContext camelContext;
  private RouteController routeController;

  @BeforeEach
  void setup() {
    camelContext = mock(CamelContext.class);
    routeController = mock(RouteController.class);

    when(camelContext.getRouteController()).thenReturn(routeController);
  }

  @Test
  void When_executeRouteOperationRESUME_Expect_resumeRouteCalled() throws Exception {
    // arrange
    RouteOperation subject = RouteOperation.RESUME;

    subject.execute(camelContext, ROUTE_ID);

    // assert
    verify(routeController, times(1)).resumeRoute(ROUTE_ID);
  }

  @Test
  void When_executeRouteOperationSTOP_Expect_stopRouteCalled() throws Exception {
    // arrange
    RouteOperation subject = RouteOperation.STOP;

    // act
    subject.execute(camelContext, ROUTE_ID);

    // assert
    verify(routeController, times(1)).stopRoute(ROUTE_ID);
  }

  @Test
  void When_executeRouteOperationSTART_Expect_stopRouteCalled() throws Exception {
    // arrange
    RouteOperation subject = RouteOperation.START;

    // act
    subject.execute(camelContext, ROUTE_ID);

    // assert
    verify(routeController, times(1)).startRoute(ROUTE_ID);
  }

  @Test
  void When_executeRouteOperationSUSPEND_Expect_stopRouteCalled() throws Exception {
    // arrange
    RouteOperation subject = RouteOperation.SUSPEND;

    // act
    subject.execute(camelContext, ROUTE_ID);

    // assert
    verify(routeController, times(1)).suspendRoute(ROUTE_ID);
  }

  @Test
  void When_executeAndOperationThrowsException_Expect_IntegrationManagementException()
      throws Exception {
    // arrange
    RouteOperation subject = RouteOperation.RESUME;
    doThrow(new Exception()).when(routeController).resumeRoute(anyString());

    // assert
    assertThatThrownBy(() -> subject.execute(camelContext, "test"))
        .isInstanceOf(IntegrationManagementException.class);
  }
}
