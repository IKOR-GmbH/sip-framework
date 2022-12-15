package de.ikor.sip.foundation.core.framework.routers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.ikor.sip.foundation.core.apps.framework.emptyapp.EmptyTestingApplication;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;

import java.util.List;

import de.ikor.sip.foundation.core.framework.stubs.routers.RestCentralRouter;
import de.ikor.sip.foundation.core.framework.stubs.routers.TestingCentralRouter;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {EmptyTestingApplication.class, RestCentralRouter.class, TestingCentralRouter.class})
class InvalidUsageOfOutConsumerTest {
  @Autowired CamelContext camelContext;
  @Autowired List<CentralRouter> centralRouters;

  @Test
  void when_OutEndpointIsUsedAsConsumer_then_RuntimeExceptionIsThrown() {
    // arrange
    RouteBuilder rb =
        new RouteBuilder() {
          @Override
          public void configure() {
            from(OutEndpoint.instance("direct:outEndpointAsConsumer", "ep-id")).to("mock:test");
          }
        };
    // act & assert
    assertThatThrownBy(() -> camelContext.addRoutes(rb))
        .hasCauseExactlyInstanceOf(IllegalAccessException.class);
  }

  @Test
  void when_MultipleCentralRoutersAreRegistered_then_ApplicationStartsSuccessfully() {
    assertThat(centralRouters).asList().size().isGreaterThan(1);
  }
}
