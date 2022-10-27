package de.ikor.sip.foundation.core.framework;

import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.framework.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.stubs.*;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
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
class CentralRoutedDataflowTest {
  @Autowired(required = false)
  private TestingCentralRouter routerSubject;

  @Autowired(required = false)
  private RouteStarter routeStarter;

  @Autowired private ProducerTemplate template;

  @EndpointInject("mock:log:message")
  private MockEndpoint mock;

  @EndpointInject("mock:log:message-testkit")
  private MockEndpoint mockTest;

  @BeforeEach
  void setup() {
    routerSubject.setupTestingState();
    mock.reset();
  }

  @Test
  void when_RouteMulticastsParallelToOneConnector_then_ConnectorForwardsTheExchange()
      throws Exception {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:multicast-1");
    SimpleOutConnector outConnector1 =
        new SimpleOutConnector().outEndpointUri("log:message").outEndpointId("ep-1");

    mock.expectedMessageCount(1);
    mock.expectedBodiesReceived("Hello dude!-[ep-1]");
    routerSubject.from(inConnector).to(outConnector1).build();

    template.sendBody("direct:multicast-1", "Hello dude!");

    mock.assertIsSatisfied();
  }

  @Test
  void
      given_RouteWithParallelMulticast_when_FirstOutConnectorIsSlow_then_SecondConnectorExecutesFirst()
          throws Exception {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:multicast-3");
    SleepingOutConnector outConnector1 =
        new SleepingOutConnector().outEndpointUri("log:message").outEndpointId("ep-1");
    SimpleOutConnector outConnector2 =
        new SimpleOutConnector().outEndpointUri("log:message").outEndpointId("ep-2");

    mock.expectedBodiesReceived("Hello dude!-[ep-2]", "Hello dude!-[ep-1]");
    mock.expectedMessageCount(2);

    routerSubject.from(inConnector).to(outConnector1, outConnector2).build();

    template.sendBody("direct:multicast-3", "Hello dude!");

    mock.assertIsSatisfied();
  }

  @Test
  void
      given_RouteWithSequencedMulticast_when_FirstOutConnectorIsSlow_then_FirstConnectorExecutesFirst()
          throws Exception {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:multicast-4");
    SleepingOutConnector outConnector1 =
        new SleepingOutConnector().outEndpointUri("log:message").outEndpointId("ep-1");
    SimpleOutConnector outConnector2 =
        new SimpleOutConnector().outEndpointUri("log:message").outEndpointId("ep-2");

    mock.expectedBodiesReceived("Hello dude!-[ep-1]", "Hello dude!-[ep-2]");
    mock.expectedMessageCount(2);
    routerSubject.from(inConnector).to(outConnector1).to(outConnector2).build();

    template.sendBody("direct:multicast-4", "Hello dude!");

    mock.assertIsSatisfied();
  }

  void // toDO make it work
      given_RouteWithParallelAndSequencedMulticast_when_FirstOutConnectorIsSlow_then_SecondConnectorExecutesFirstAndThirdLast()
      throws Exception {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:multicast-5");
    SleepingOutConnector outConnector1 =
        new SleepingOutConnector().outEndpointUri("log:message").outEndpointId("ep-1");
    SimpleOutConnector outConnector2 =
        new SimpleOutConnector().outEndpointUri("log:message").outEndpointId("ep-2");
    SimpleOutConnector outConnector3 =
        new SimpleOutConnector().outEndpointUri("log:message").outEndpointId("ep-3");

    mock.expectedBodiesReceived("Hello dude!-[ep-2]", "Hello dude!-[ep-1]", "Hello dude!-[ep-3]");
    mock.expectedMessageCount(3);

    routerSubject.from(inConnector).to(outConnector1, outConnector2).to(outConnector3).build();

    template.sendBody("direct:multicast-5", "Hello dude!");

    mock.assertIsSatisfied();
  }

  @Test
  void when_RouteUsesEnrich_then_SIPMetadataIsSupported() throws Exception {
    CentralRouter.getCamelContext().addRoutes(routeBuilder());
    mock.expectedBodiesReceived("yes, enrich works");
    template.sendBody("direct:withEnrich", "enrichWorks?");

    mock.assertIsSatisfied();
  }

  @Test
  void when_InConnectorImplementsResponseProcessing_then_ConnectorReturnsProcessedResponse()
      throws Exception {
    CentralRouter.getCamelContext().addRoutes(ComplexOutConnector.helperRouteBuilder);

    routerSubject.from(new ComplexInConnector()).to(new ComplexOutConnector()).build();

    // act
    String response =
        (String) template.sendBody("direct:complex-connector", ExchangePattern.InOut, "input body");

    assertThat(response).endsWith("voila").contains("body 1").contains("body 2");
    mock.assertIsSatisfied();
  }

  @Test
  void given_TestingStateIsActive_when_TriggerTestingRoute_then_TestEndpointInvoked()
      throws Exception {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:multicast-6");
    SleepingOutConnector outConnector =
        new SleepingOutConnector().outEndpointUri("log:message").outEndpointId("ep-1");

    mockTest.expectedBodiesReceived("Hello dude!-[ep-1]");
    mockTest.expectedMessageCount(1);

    routerSubject.from(inConnector).to(outConnector).build();

    CentralEndpointsRegister.setState("testing");
    template.sendBody("direct:multicast-6-testkit", "Hello dude!");

    mockTest.assertIsSatisfied();
    CentralEndpointsRegister.setState("actual");
  }

  private RoutesBuilder routeBuilder() {
    return new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        from("direct:withEnrich")
            .enrich(OutEndpointBuilder.outEndpointBuilder("direct:test-enrich", ""))
            .to("log:message");

        from("direct:oho").process();
      }
    };
  }
}
