package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector.withUri;
import static org.assertj.core.api.Assertions.*;

import de.ikor.sip.foundation.core.apps.framework.centralrouter.EmptyTestingApplication;
import de.ikor.sip.foundation.core.framework.stubs.routers.TestingCentralRouter;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpointBuilder;
import de.ikor.sip.foundation.core.framework.stubs.*;
import de.ikor.sip.foundation.core.framework.testutil.TestSetupUtil;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.DisableJmx;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@CamelSpringBootTest
@SpringBootTest(classes = {EmptyTestingApplication.class, EnrichRouteConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisableJmx(false)
@MockEndpoints("log:message*")
class CentralRoutedDataflowTest {

  private final TestingCentralRouter routerSubject = new TestingCentralRouter();
  private String inEndpointUri;
  @Autowired private ProducerTemplate template;

  @EndpointInject("mock:log:message")
  private MockEndpoint loggerMock;

  @BeforeEach
  void setup() {
    loggerMock.reset();
    inEndpointUri = TestSetupUtil.getNextEndpointId();
  }

  @AfterEach
  void assertTest() throws InterruptedException {
    loggerMock.assertIsSatisfied();
  }

  @Test
  void when_RouteMulticastsParallelToOneConnector_then_ConnectorForwardsTheExchange() {
    setupRouterWithParallel(outLogConn("ep-1"));

    sendMessageToInEndpoint("Hello dude!");

    loggerMock.expectedMessageCount(1);
    loggerMock.expectedBodiesReceived("Hello dude!-[ep-1]");
  }

  @Test
  void when_RouteMulticastsParallelToTwoConnectors_then_ConnectorsForwardsTheExchange() {
    setupRouterWithParallel(outLogConn("ep-1"), outLogConn("ep-2"));

    loggerMock.expectedMessageCount(2);
    loggerMock.expectedBodiesReceivedInAnyOrder("Hello dudes!-[ep-1]", "Hello dudes!-[ep-2]");

    sendMessageToInEndpoint("Hello dudes!");
  }

  @Test
  void
      given_RouteWithParallelMulticast_when_FirstOutConnectorIsSlow_then_SecondConnectorExecutesFirst() {
    setupRouterWithParallel(sleepingOutLogConn("ep-1"), outLogConn("ep-2"));

    sendMessageToInEndpoint("Hello dude!");

    loggerMock.expectedBodiesReceived("Hello dude!-[ep-2]", "Hello dude!-[ep-1]");
    loggerMock.expectedMessageCount(2);
  }

  @Test
  void
      given_RouteWithSequencedExecution_when_FirstOutConnectorIsSlow_then_FirstConnectorExecutesFirst() {
    setupRouterWithSequence(sleepingOutLogConn("ep-1"), outLogConn("ep-2"));

    loggerMock.expectedBodiesReceived("Hello dude!-[ep-1]", "Hello dude!-[ep-2]");
    loggerMock.expectedMessageCount(2);

    sendMessageToInEndpoint("Hello dude!");
  }

  @Test
  void when_RouteUsesEnrich_then_SIPMetadataIsSupported() throws Exception {
    startEnrichRouteWithOutEndpointBuilder();
    loggerMock.expectedBodiesReceived("yes, enrich works");
    template.sendBody("direct:withEnrich", "enrichWorks?");
  }

  @Test
  void when_InConnectorImplementsResponseProcessing_then_ConnectorReturnsProcessedResponse()
      throws Exception {
    // arrange
    camelContext().addRoutes(ComplexOutConnector.helperRouteBuilder);
    routerSubject.input(new ComplexInConnector()).sequencedOutput(new ComplexOutConnector());
    startTheRouter(routerSubject);
    // act
    String response =
        (String) template.sendBody("direct:complex-connector", ExchangePattern.InOut, "input body");
    // assert
    assertThat(response).endsWith("voila").contains("body 1").contains("body 2");
  }

  @Test
  void when_CentralRouterHasNoDomainModel_thenExceptionIsThrown() {
    CentralRouter noCentralModelRouterSubject = new WrongTypeRouter();

    // act
    noCentralModelRouterSubject.toCentralRouter().setUpRoutes();

    // assert
    assertThatThrownBy(() -> template.sendBody("direct:multicast-7", "Hello dude!"))
        .isInstanceOf(CamelExecutionException.class)
        .getCause()
        .hasMessageContaining("Wrong data type")
        .hasMessageContaining("WrongTypeRouter");
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

  private OutConnector outLogConn(String id) {
    return new SimpleOutConnector().withUri("log:message").withId(id);
  }

  private OutConnector sleepingOutLogConn(String id) {
    return new SleepingOutConnector().withUri("log:message").withId(id);
  }

  private void sendMessageToInEndpoint(String message) {
    template.sendBody(inEndpointUri, message);
  }

  private void startTheRouter(CentralRouter routerSubject) {
    routerSubject.toCentralRouter().setUpRoutes();
  }

  private void setupRouterWithParallel(OutConnector... outConnectors) {
    routerSubject.input(withUri(inEndpointUri)).parallelOutput(outConnectors);
    routerSubject.toCentralRouter().setUpRoutes();
  }

  private void setupRouterWithSequence(OutConnector... outConnectors) {
    routerSubject.input(withUri(inEndpointUri)).sequencedOutput(outConnectors);
    routerSubject.toCentralRouter().setUpRoutes();
  }

  private void startEnrichRouteWithOutEndpointBuilder() throws Exception {
    camelContext().addRoutes(routeBuilder());
  }
}
