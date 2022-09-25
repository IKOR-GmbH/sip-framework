package de.ikor.sip.foundation.core.framework;

import de.ikor.sip.foundation.core.apps.framework.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouter;
import org.apache.camel.Endpoint;
import org.apache.camel.NoSuchEndpointException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CentralRouterTestingApplication.class)
class EndpointsIntegrationTests {
  @Autowired(required = false)
  private TestingCentralRouter subject;

  @Test
  void when_OutEndpointIsOnTheRoute_then_OutEndpointHasProperId() throws Exception {
    // arrange
    SimpleOutConnector outConnector = new SimpleOutConnector("cool-id");

    // act
    subject.from(new SimpleInConnector("direct:hey")).to(outConnector);
    // assert

    Endpoint registeredEndpoint = CentralOutEndpointsRegister.getEndpoint("cool-id");
    Endpoint endpointFromCamelContext = null;
    try {
      endpointFromCamelContext = CentralRouter.getCamelContext().getEndpoint(registeredEndpoint.getEndpointUri());
    } catch (NoSuchEndpointException e) {
      // just ignore
    }
    assertThat(endpointFromCamelContext).isNotNull();
  }
}