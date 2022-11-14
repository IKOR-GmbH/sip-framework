package de.ikor.sip.foundation.core.framework.routers;

import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.framework.centralrouter.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.stubs.TestingOutConnector;
import org.apache.camel.Endpoint;
import org.apache.camel.NoSuchEndpointException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = CentralRouterTestingApplication.class)
@DirtiesContext
class EndpointsIntegrationTests {
  private TestingCentralRouterDefinition subject = new TestingCentralRouterDefinition();
  
  @Autowired
  private RouteStarter starter;
  @Test
  void when_OutEndpointIsOnTheRoute_then_OutEndpointHasProperId() throws Exception {
    // arrange
    SimpleOutConnector outConnector = new SimpleOutConnector().outEndpointId("cool-id");

    // act
    subject.input(SimpleInConnector.withUri("direct:hey-test")).sequencedOutput(outConnector);
    starter.buildRoutes(subject.toCentralRouter());
    // assert

    Endpoint registeredEndpoint = CentralEndpointsRegister.getEndpoint("cool-id");
    Endpoint endpointFromCamelContext = null;
    try {
      endpointFromCamelContext =
          CentralEndpointsRegister.getCamelEndpoint(registeredEndpoint.getEndpointUri());
    } catch (NoSuchEndpointException e) {
      // just ignore
    }
    assertThat(endpointFromCamelContext).isNotNull();
  }

  @Test
  void when_OutEndpointIsOnTheRoute_then_TestingOutEndpointHasProperURI() throws Exception {
    // arrange

    TestingOutConnector outConnector = new SimpleOutConnector().outEndpointId("cool-id");

    // act
    subject.input(SimpleInConnector.withUri("sipmc:hey-test")).sequencedOutput(outConnector);
    // assert

    CentralEndpointsRegister.putInTestingState();
    Endpoint registeredEndpoint = CentralEndpointsRegister.getEndpoint("cool-id");
    Endpoint endpointFromCamelContext = null;
    try {
      endpointFromCamelContext =
          CentralEndpointsRegister.getCamelEndpoint(registeredEndpoint.getEndpointUri());
    } catch (NoSuchEndpointException e) {
      // just ignore
    }
    assertThat(registeredEndpoint.getEndpointUri()).endsWith("-testkit");
    assertThat(endpointFromCamelContext).isNotNull();
    CentralEndpointsRegister.putInActualState();
  }
}
