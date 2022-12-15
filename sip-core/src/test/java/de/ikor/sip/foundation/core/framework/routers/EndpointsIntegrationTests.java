package de.ikor.sip.foundation.core.framework.routers;

import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.framework.emptyapp.EmptyTestingApplication;
import de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister;
import de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.routers.TestingCentralRouter;
import org.apache.camel.Endpoint;
import org.apache.camel.NoSuchEndpointException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = EmptyTestingApplication.class)
@DirtiesContext
class EndpointsIntegrationTests {
  private final TestingCentralRouter subject = new TestingCentralRouter();

  @Test
  void when_OutEndpointIsOnTheRoute_then_OutEndpointHasProperId() {
    // arrange
    SimpleOutConnector outConnector = new SimpleOutConnector().withId("cool-id");

    // act
    subject.input(SimpleInConnector.withUri("direct:hey-test")).sequencedOutput(outConnector);
    subject.toCentralRouter().setUpRoutes();

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
