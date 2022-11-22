package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.framework.centralrouter.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouterDefinition;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.camel.Route;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = {CentralRouterTestingApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CentralRouterStructureTest {
  @Autowired private TestingCentralRouterDefinition testingCentralRouterDefinition;

  @Autowired(required = false)
  private RouteStarter routeStarter;

  @BeforeEach
  void setup() {
    testingCentralRouterDefinition.setupTestingState();
  }

  @Test
  void when_ApplicationStarts_then_CentralRouterBeanIsLoaded() {
    // TODO this test should check on routerSubject bean availability. routerSubject should be bean
    assertThat(testingCentralRouterDefinition).as("CentralRouter bean not initialized").isNotNull();
    Assertions.assertThat(camelContext()).as("Camel context not set on CentralRouter").isNotNull();
  }

  @Test
  void when_SingleInConnectorIsRegistered_then_OneRouteStartingWithProperEndpointIsAvailable()
      throws Exception {
    // arrange
    SimpleInConnector simpleInConnector = SimpleInConnector.withUri("direct:singleInConnector");
    // act
    testingCentralRouterDefinition.input(simpleInConnector);
    routeStarter.buildRoutes(testingCentralRouterDefinition.toCentralRouter());
    // assert
    assertThat(getRoutesFromContext())
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
    SimpleInConnector firstInConnector = SimpleInConnector.withUri("direct:sip");
    SimpleInConnector secondInConnector = SimpleInConnector.withUri("direct:sipie");
    // act
    testingCentralRouterDefinition.input(firstInConnector, secondInConnector);
    routeStarter.buildRoutes(testingCentralRouterDefinition.toCentralRouter());
    // assert
    assertThat(getRoutesFromContext()).filteredOn(matchRoutesBasedOnUri("direct.*sip")).hasSize(1);

    assertThat(getRoutesFromContext())
        .filteredOn(matchRoutesBasedOnUri("direct.*sipie"))
        .hasSize(1);

    assertThat(getRoutesFromContext())
        .filteredOn(matchRoutesBasedOnUri("direct.*sip-testkit"))
        .hasSize(1);
  }

  @Test
  void when_OneOutConnectorIsRegistered_then_OneRouteWithSIPMCEndpointIsAvailable()
      throws Exception {
    // arrange
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:OneOutConnector");
    SimpleOutConnector outConnector = new SimpleOutConnector();

    // act
    testingCentralRouterDefinition.input(inConnector).sequencedOutput(outConnector);
    routeStarter.buildRoutes(testingCentralRouterDefinition.toCentralRouter());

    // assert
    assertThat(getRoutesFromContext())
        .filteredOn(matchRoutesBasedOnUri(format("sipmc.*%s", testingCentralRouterDefinition.getScenario())))
        .as("Exactly one OutConnectors is expected.")
        .hasSize(1);
  }

  @Test
  void
      given_OneInConnectorWithOneRoute_when_ConnectorIsRegistered_then_RouteIdIsUseCasePlusConnectorName()
          throws Exception {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:routeIdTest");
    testingCentralRouterDefinition.input(inConnector);
    routeStarter.buildRoutes(testingCentralRouterDefinition.toCentralRouter());

    String expectedRouteId = format("%s-%s", testingCentralRouterDefinition.getScenario(), inConnector.getName());
    Route route = getRouteFromContextById(expectedRouteId);

    assertThat(route)
        .as(
            "Expected route with Id: %s %sDetected: %s",
            expectedRouteId,
            System.lineSeparator(),
            getRoutesFromContext().stream().map(Route::getId).collect(Collectors.joining(", ")))
        .isNotNull();
  }

  @Test
  void when_ApplicationStarts_then_CentralRouterBeanIsConfigured() throws Exception {
    assertThat(testingCentralRouterDefinition.isConfigured).isTrue();
  }

  @Test
  void when_AppStarts_then_RouteStarterIsInitialized() {
    assertThat(routeStarter).as("RouteStarter bean is not initialized").isNotNull();
  }

  @Test
  void given_CentralRouterBeansArePresent_when_AppStarts_then_RoutStarterHasRouters() {
    assertThat(routeStarter.availableRouters).isNotEmpty();
  }

  @Test
  void when_TopologyIsDefined_expect_DifferentAPIForParallelOutConnectors() {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:unimportant");
    OutConnector parallelOut1 = new SimpleOutConnector();
    OutConnector parallelOut2 = new SimpleOutConnector();
    testingCentralRouterDefinition.input(inConnector).parallelOutput(parallelOut1, parallelOut2);
  }

  public static Predicate<Route> matchRoutesBasedOnUri(String regex) {
    return route -> route.getEndpoint().getEndpointUri().matches(regex);
  }

  private List<Route> getRoutesFromContext() {
    return camelContext().getRoutes();
  }

  private Route getRouteFromContextById(String routeId) {
    return camelContext().getRoute(routeId);
  }
}
