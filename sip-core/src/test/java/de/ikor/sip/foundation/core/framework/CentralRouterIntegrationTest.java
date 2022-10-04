package de.ikor.sip.foundation.core.framework;

import de.ikor.sip.foundation.core.apps.framework.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouter;
import org.apache.camel.Route;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Predicate;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {CentralRouterTestingApplication.class})
class CentralRouterIntegrationTest {
  @Autowired(required = false)
  private TestingCentralRouter subject;
  @Autowired(required = false)
  private RouteStarter routeStarter;

  @BeforeEach
  void setup() {
    subject.setupTestingState();
  }

  @Test
  void when_ApplicationStarts_then_CentralRouterBeanIsLoaded() {
    assertThat(subject).as("CentralRouter bean not initialized").isNotNull();
    Assertions.assertThat(CentralRouter.getCamelContext())
        .as("Camel context not set on CentralRouter")
        .isNotNull();
  }

  @Test
  void when_SingleInConnectorIsRegistered_then_OneRouteStartingWithProperEndpointIsAvailable()
      throws Exception {
    // arrange
    SimpleInConnector simpleInConnector = new SimpleInConnector("direct:singleInConnector");
    // act
    subject.from(simpleInConnector);
    // assert
    assertThat(CentralRouter.getCamelContext().getRoutes())
        .filteredOn(matchRoutesBasedOnUri("direct.*singleInConnector"))
        .as(
            "Test connector is not registered. Expected %s endpoint",
            simpleInConnector.getEndpointUri())
        .hasSize(1);
  }

  @Test
  void when_MultipleInConnectorsAreRegistered_then_OneRoutePerConnectorIsAvailable()
      throws Exception {
    // arrange
    SimpleInConnector firstInConnector = new SimpleInConnector("direct:sip");
    SimpleInConnector secondInConnector = new SimpleInConnector("direct:sipie");
    // act
    subject.from(firstInConnector, secondInConnector);
    // assert
    assertThat(CentralRouter.getCamelContext().getRoutes())
        .filteredOn(matchRoutesBasedOnUri("direct.*sip.*"))
        .as("One or more connectors is not registered.")
        .hasSize(2);
  }

  @Test
  void when_OneOutConnectorIsRegistered_then_OneRouteWithSIPMCEndpointIsAvailable()
      throws Exception {
    // arrange
    SimpleInConnector inConnector = new SimpleInConnector("direct:OneOutConnector");
    SimpleOutConnector outConnector = new SimpleOutConnector();
    // act
    subject.from(inConnector).to(outConnector);

    // assert
    assertThat(CentralRouter.getCamelContext().getRoutes())
        .filteredOn(matchRoutesBasedOnUri(format("sipmc.*%s", subject.getUseCase())))
        .as("No OutConnectors registered.")
        .hasSize(1);
  }

  @Test
  void when_MultipleOutConnectorsAreRegistered() throws Exception {
    // arrange
    SimpleInConnector inConnector = new SimpleInConnector("direct:multipleOutConnectors");
    SimpleOutConnector outConnector1 = new SimpleOutConnector();
    SimpleOutConnector outConnector2 = new SimpleOutConnector();
    // act
    subject.from(inConnector).to(outConnector1).to(outConnector2);

    // assert
    assertThat(CentralRouter.getCamelContext().getRoutes())
        .filteredOn(matchRoutesBasedOnUri(format("sipmc.*%s", subject.getUseCase())))
        .hasSize(2);
  }

  @Test
  void
      given_OneInConnectorWithOneRoute_when_ConnectorIsRegistered_then_RouteIdIsUseCasePlusConnectorName()
          throws Exception {
    SimpleInConnector inConnector = new SimpleInConnector("direct:routeIdTest");
    subject.from(inConnector);
    Route route = CentralRouter.getCamelContext()
            .getRoute(format("%s-%s", subject.getUseCase(), inConnector.getName()));

    assertThat(route).isNotNull();
  }

  @Test
  void when_ApplicationStarts_then_CentralRouterBeanIsConfigured() throws Exception {
    AssertionsForClassTypes.assertThat(subject.isConfigured)
            .isTrue();
  }

  @Test
  void when_AppStarts_then_RouteStarterIsInitialized() {
    assertThat(routeStarter).
        as("RouteStarter bean is not initialized")
            .isNotNull();
  }

  @Test
  void given_CentralRouterBeansArePresent_when_AppStarts_then_RoutStarterHasRouters() {
    assertThat(routeStarter.availableRouters)
            .isNotEmpty();
  }

  public static Predicate<Route> matchRoutesBasedOnUri(String regex) {
    return route -> route.getEndpoint().getEndpointUri().matches(regex);
  }
}
