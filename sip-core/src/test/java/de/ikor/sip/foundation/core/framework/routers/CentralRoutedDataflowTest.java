package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static org.assertj.core.api.Assertions.*;

import de.ikor.sip.foundation.core.apps.framework.centralrouter.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpointBuilder;
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
import org.springframework.test.annotation.DirtiesContext;

@CamelSpringBootTest
@SpringBootTest(classes = {CentralRouterTestingApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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

    routerSubject.input(inConnector).sequencedOutput(outConnector1);
    routerSubject.toCentralRouter().setUpRoutes();

    template.sendBody("direct:multicast-1", "Hello dude!");
    mock.assertIsSatisfied();
  }

  @Test
  void when_RouteMulticastsParallelToTwoConnectors_then_ConnectorsForwardsTheExchange()
      throws Exception {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:multicast-1");

    SimpleOutConnector outConnector1 =
        new SimpleOutConnector().outEndpointUri("log:message").outEndpointId("ep-1");
    SimpleOutConnector outConnector2 =
        new SimpleOutConnector().outEndpointUri("log:message").outEndpointId("ep-2");

    mock.expectedMessageCount(2);
    mock.expectedBodiesReceivedInAnyOrder("Hello dudes!-[ep-1]", "Hello dudes!-[ep-2]");

    routerSubject.input(inConnector).parallelOutput(outConnector1, outConnector2);
    routerSubject.toCentralRouter().setUpRoutes();

    template.sendBody("direct:multicast-1", "Hello dudes!");
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

    routerSubject.input(inConnector).parallelOutput(outConnector1, outConnector2);
    routerSubject.toCentralRouter().setUpRoutes();

    template.sendBody("direct:multicast-3", "Hello dude!");

    mock.assertIsSatisfied();
  }

  @Test
  void
      given_RouteWithSequencedExecution_when_FirstOutConnectorIsSlow_then_FirstConnectorExecutesFirst()
          throws Exception {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:multicast-4");

    SleepingOutConnector outConnector1 =
        new SleepingOutConnector().outEndpointUri("log:message").outEndpointId("ep-1");
    SimpleOutConnector outConnector2 =
        new SimpleOutConnector().outEndpointUri("log:message").outEndpointId("ep-2");

    mock.expectedBodiesReceived("Hello dude!-[ep-1]", "Hello dude!-[ep-2]");
    mock.expectedMessageCount(2);

    routerSubject.input(inConnector).sequencedOutput(outConnector1, outConnector2);
    routerSubject.toCentralRouter().setUpRoutes();

    template.sendBody("direct:multicast-4", "Hello dude!");

    mock.assertIsSatisfied();
  }

  @Test
  void when_RouteUsesEnrich_then_SIPMetadataIsSupported() throws Exception {
    camelContext().addRoutes(routeBuilder());
    mock.expectedBodiesReceived("yes, enrich works");
    template.sendBody("direct:withEnrich", "enrichWorks?");

    mock.assertIsSatisfied();
  }

  @Test
  void when_InConnectorImplementsResponseProcessing_then_ConnectorReturnsProcessedResponse()
      throws Exception {
    camelContext().addRoutes(ComplexOutConnector.helperRouteBuilder);

    routerSubject.input(new ComplexInConnector()).sequencedOutput(new ComplexOutConnector());

    // act
    routerSubject.toCentralRouter().setUpRoutes();
    String response =
        (String) template.sendBody("direct:complex-connector", ExchangePattern.InOut, "input body");

    assertThat(response).endsWith("voila").contains("body 1").contains("body 2");
    mock.assertIsSatisfied();
  }

  //  @Test TODO optional validation
  //  void when_CentralRouterHasNoDomainModel_thenExceptionIsThrown() {
  //    CentralRouterDefinition noCentralModelRouterSubject = new WrongTypeRouterDefinition();
  //
  //    // act
  //    routeStarter.configureDefinition(noCentralModelRouterSubject);
  //    routeStarter.buildRoutes(noCentralModelRouterSubject.toCentralRouter());
  //
  //    // assert
  //    assertThatThrownBy(() -> template.sendBody("direct:multicast-7", "Hello dude!"))
  //        .isInstanceOf(CamelExecutionException.class)
  //        .getCause()
  //        .hasMessageContaining("Wrong data type")
  //        .hasMessageContaining("WrongTypeRouter");
  //  }

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
