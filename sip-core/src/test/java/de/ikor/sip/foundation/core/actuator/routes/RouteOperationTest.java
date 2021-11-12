package de.ikor.sip.foundation.core.actuator.routes;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.Test;

class RouteOperationTest {

  @Test
  void execute_RESUME() {
    RouteOperation subject = RouteOperation.RESUME;
    CamelContext camelContext = mock(CamelContext.class);
    RouteController routeController = mock(RouteController.class);
    when(camelContext.getRouteController()).thenReturn(routeController);

    assertThatCode(() -> subject.execute(camelContext, "test")).doesNotThrowAnyException();
  }

  @Test
  void execute_ThrowException() throws Exception {
    // arrange
    RouteOperation subject = RouteOperation.RESUME;
    CamelContext camelContext = mock(CamelContext.class);
    RouteController routeController = mock(RouteController.class);
    when(camelContext.getRouteController()).thenReturn(routeController);

    // act
    doThrow(new Exception()).when(routeController).resumeRoute(anyString());

    // assert
    assertThatThrownBy(() -> subject.execute(camelContext, "test")).isInstanceOf(Exception.class);
  }
}
