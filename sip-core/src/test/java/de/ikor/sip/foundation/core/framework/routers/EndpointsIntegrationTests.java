package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.apps.framework.centralrouter.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouterDefinition;
import org.apache.camel.Endpoint;
import org.apache.camel.NoSuchEndpointException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CentralRouterTestingApplication.class)
@DirtiesContext
class EndpointsIntegrationTests {
  private final TestingCentralRouterDefinition subject = new TestingCentralRouterDefinition();

  @Autowired private RouteStarter starter;

  @Test
  void when_OutEndpointIsOnTheRoute_then_OutEndpointHasProperId() throws Exception {
    // arrange
    SimpleOutConnector outConnector = new SimpleOutConnector().outEndpointId("cool-id");

    // act
    subject.input(SimpleInConnector.withUri("direct:hey-test")).sequencedOutput(outConnector);
    starter.buildRoutes(subject.toCentralRouter());

    // assert
    Endpoint registeredEndpoint = CentralEndpointsRegister.getOutEndpoint("cool-id");
    Endpoint endpointFromCamelContext = null;
    try {
      endpointFromCamelContext =
          CentralEndpointsRegister.getCamelEndpoint(registeredEndpoint.getEndpointUri());
    } catch (NoSuchEndpointException e) {
      // just ignore
    }
    assertThat(endpointFromCamelContext).isNotNull();
  }
}
