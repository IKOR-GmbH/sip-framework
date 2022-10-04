package de.ikor.sip.foundation.core.framework;

import de.ikor.sip.foundation.core.apps.framework.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouter;
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

@CamelSpringBootTest
@SpringBootTest(classes = {CentralRouterTestingApplication.class})
@DisableJmx(false)
@MockEndpoints("log:message*")
class MulticastTest {
  @Autowired(required = false)
  private TestingCentralRouter subject;

  @Autowired(required = false)
  private RouteStarter routeStarter;

  @Autowired private ProducerTemplate template;

  @EndpointInject("mock:log:message")
  private MockEndpoint mock;

  @EndpointInject("mock:log:message-2")
  private MockEndpoint mockLogMessage2;

  @EndpointInject("mock:log:message-3")
  private MockEndpoint mockLogMessage3;

  @BeforeEach
  void setup() {
    subject.setupTestingState();
  }

  @Test
  void when_RouteMulticastsParallelToOneConnector_then_ConnectorForwardsTheExchange()
      throws Exception {
    SimpleInConnector inConnector = new SimpleInConnector("direct:multicast-1");
    SimpleOutConnector outConnector1 = new SimpleOutConnector("log:message", "ep-1");

    subject.from(inConnector).to(outConnector1);
    mock.expectedBodiesReceived("Hello dude!-[ep-1]");
    mock.expectedMessageCount(1);

    template.sendBody("direct:multicast-1", "Hello dude!");

    mock.assertIsSatisfied();
  }

  @Test
  void when_RouteMulticastsParallelToTwoConnectors_then_BothConnectorForwardsTheExchange()
      throws Exception {
    SimpleInConnector inConnector = new SimpleInConnector("direct:multicast-2");
    SimpleOutConnector outConnector1 = new SimpleOutConnector("log:message-2", "ep-2");
    SimpleOutConnector outConnector2 = new SimpleOutConnector("log:message-3", "ep-3");

    mockLogMessage2.expectedBodiesReceived("Hello dude!-[ep-2]");
    mockLogMessage2.expectedMessageCount(1);
    mockLogMessage3.expectedBodiesReceived("Hello dude!-[ep-3]");
    mockLogMessage3.expectedMessageCount(1);
    subject.from(inConnector)
            .to(outConnector1, outConnector2);

    template.sendBody("direct:multicast-2", "Hello dude!");

    mockLogMessage2.assertIsSatisfied();
    mockLogMessage3.assertIsSatisfied();
  }

  @Test
  void when_RouteMulticastsToGroups_then_BothConnectorForwardsTheExchange()
          throws Exception {
    SimpleInConnector inConnector = new SimpleInConnector("direct:multicast-3");
    SimpleOutConnector outConnector1 = new SimpleOutConnector("log:message-2", "ep-2");
    SimpleOutConnector outConnector2 = new SimpleOutConnector("log:message-3", "ep-3");

    mockLogMessage2.reset();
    mockLogMessage3.reset();

    mockLogMessage2.expectedBodiesReceived("Hello dude!-[ep-2]");
    mockLogMessage2.expectedMessageCount(1);
    mockLogMessage3.expectedBodiesReceived("Hello dude!-[ep-3]");
    mockLogMessage3.expectedMessageCount(1);
    subject.from(inConnector)
            .to(outConnector1, outConnector2);

    template.sendBody("direct:multicast-3", "Hello dude!");

    mockLogMessage2.assertIsSatisfied();
    mockLogMessage3.assertIsSatisfied();
  }
}
