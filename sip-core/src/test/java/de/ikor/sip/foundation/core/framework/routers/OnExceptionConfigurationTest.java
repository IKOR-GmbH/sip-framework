package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.apps.core.CoreTestApplication;
import de.ikor.sip.foundation.core.framework.connectors.InConnectorDefinition;
import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;
import de.ikor.sip.foundation.core.framework.stubs.*;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.DisableJmx;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@CamelSpringBootTest
@SpringBootTest(classes = CoreTestApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@DisableJmx(false)
@MockEndpoints("log:message*")
class OnExceptionConfigurationTest {

  private TestingCentralRouterDefinition routerSubject = new TestingCentralRouterDefinition();

  @Autowired(required = false)
  private RouteStarter routeStarter;

  @Autowired private ProducerTemplate template;

  @EndpointInject("mock:log:message")
  private MockEndpoint mock;

  @BeforeEach
  void setup() {
    mock.reset();
  }

  @Test
  void when_OnExceptionConfiguredOnInConnector_then_ValidateInConnectorOnExceptionExecution()
      throws Exception {
    // arrange
    InConnectorDefinition inConnectorDefinition =
        new OnExceptionInConnector(InEndpoint.instance("direct:inDirect-1", "inDirect-1"));
    routerSubject.input(inConnectorDefinition);
    routeStarter.buildRoutes(routerSubject.toCentralRouter());

    mock.expectedBodiesReceived("InConnectorException");
    mock.expectedMessageCount(1);

    // act
    template.sendBody("direct:inDirect-1", "input body");

    // assert
    mock.assertIsSatisfied();
  }

  @Test
  void when_OnExceptionConfiguredOnOutConnector_then_ValidateOutConnectorOnExceptionExecution()
      throws Exception {
    // arrange
    InConnectorDefinition inConnectorDefinition = SimpleInConnector.withUri("direct:inDirect-2");
    OutConnectorDefinition outConnectorDefinition =
        new OnExceptionOutConnector(OutEndpoint.instance("direct:outDirect", "outDirect-2"));
    routerSubject.input(inConnectorDefinition).sequencedOutput(outConnectorDefinition);
    routeStarter.buildRoutes(routerSubject.toCentralRouter());

    mock.expectedBodiesReceived("OutConnectorException");
    mock.expectedMessageCount(1);

    // act
    template.sendBody("direct:inDirect-2", "input body");

    // assert
    mock.assertIsSatisfied();
  }
}
