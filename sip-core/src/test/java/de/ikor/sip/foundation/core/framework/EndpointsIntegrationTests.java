package de.ikor.sip.foundation.core.framework;

import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.framework.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouter;
import de.ikor.sip.foundation.core.framework.stubs.TestingOutConnector;
import org.apache.camel.Endpoint;
import org.apache.camel.NoSuchEndpointException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CentralRouterTestingApplication.class)
class EndpointsIntegrationTests {
  @Autowired(required = false)
  private TestingCentralRouter subject;

  @Test
  void when_OutEndpointIsOnTheRoute_then_OutEndpointHasProperId() throws Exception {
    // arrange
    SimpleOutConnector outConnector = new SimpleOutConnector().outEndpointId("cool-id");

    // act
    subject.from(SimpleInConnector.withUri("direct:hey-test")).to(outConnector);
    // assert

    Endpoint registeredEndpoint = CentralEndpointsRegister.getEndpoint("cool-id");
    Endpoint endpointFromCamelContext = null;
    try {
      endpointFromCamelContext =
          CentralRouter.getCamelContext().getEndpoint(registeredEndpoint.getEndpointUri());
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
    subject.from(SimpleInConnector.withUri("sipmc:hey-test")).to(outConnector);
    // assert

    CentralEndpointsRegister.setState("testing");
    Endpoint registeredEndpoint = CentralEndpointsRegister.getEndpoint("cool-id");
    Endpoint endpointFromCamelContext = null;
    try {
      endpointFromCamelContext =
          CentralRouter.getCamelContext().getEndpoint(registeredEndpoint.getEndpointUri());
    } catch (NoSuchEndpointException e) {
      // just ignore
    }
    assertThat(registeredEndpoint.getEndpointUri()).endsWith("-test");
    assertThat(endpointFromCamelContext).isNotNull();
    CentralEndpointsRegister.setState("actual");
  }
}
