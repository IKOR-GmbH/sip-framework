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
  @Autowired private TestingCentralRouterDefinition routerSubject;

  @Autowired(required = false)
  private RouteStarter routeStarter;

  @BeforeEach
  void setup() {
    routerSubject.setupTestingState();
  }

  @Test
  void when_ApplicationStarts_then_CentralRouterBeanIsLoaded() {
    // TODO this test should check on routerSubject bean availability. routerSubject should be bean
    assertThat(routerSubject).as("CentralRouter bean not initialized").isNotNull();
    Assertions.assertThat(camelContext()).as("Camel context not set on CentralRouter").isNotNull();
  }

  @Test
  void when_SingleInConnectorIsRegistered_then_OneRouteStartingWithProperEndpointIsAvailable()
      throws Exception {
    // arrange
    SimpleInConnector simpleInConnector = SimpleInConnector.withUri("direct:singleInConnector");
    // act
    routerSubject.input(simpleInConnector);
    routeStarter.buildRoutes(routerSubject.toCentralRouter());
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
    routerSubject.input(firstInConnector, secondInConnector);
    routeStarter.buildRoutes(routerSubject.toCentralRouter());
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
  void when_OneOutConnectorIsRegisteredAsSequenced_then_OneActiveAndOneTestRouteWithSIPMCEndpointIsAvailable()
      throws Exception {
    // arrange
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:OneOutConnector");
    SimpleOutConnector outConnector = new SimpleOutConnector();

    // act
    routerSubject.input(inConnector).sequencedOutput(outConnector);
    routeStarter.buildRoutes(routerSubject.toCentralRouter());

    // assert
    assertThat(getRoutesFromContext())
        .filteredOn(matchRoutesBasedOnUri(format("sipmc.*%s", routerSubject.getScenario())))
        .as("Connector reistered via sequencedOutput - Exactly one actual OutConnector is expected.")
        .hasSize(1);

    assertThat(getRoutesFromContext())
            .filteredOn(matchRoutesBasedOnUri(format("sipmc.*%s%s", routerSubject.getScenario(), "-testkit")))
            .as("Connector reistered via sequencedOutput - Exactly one testing OutConnector is expected.")
            .hasSize(1);
  }

  @Test
  void when_OneOutConnectorIsRegisteredAsParrallel_then_OneRouteWithSIPMCEndpointIsAvailable()
          throws Exception {
    // arrange
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:OneOutConnector");
    SimpleOutConnector outConnector = new SimpleOutConnector();

    // act
    routerSubject.input(inConnector).parallelOutput(outConnector);
    routeStarter.buildRoutes(routerSubject.toCentralRouter());

    // assert
    assertThat(getRoutesFromContext())
            .filteredOn(matchRoutesBasedOnUri(format("sipmc.*%s", routerSubject.getScenario())))
            .as("Connector reistered via parallelOutput - Exactly one actual OutConnector is expected.")
            .hasSize(1);

    assertThat(getRoutesFromContext())
            .filteredOn(matchRoutesBasedOnUri(format("sipmc.*%s%s", routerSubject.getScenario(), "-testkit")))
            .as("Connector reistered via parallelOutput - Exactly one testing OutConnector is expected.")
            .hasSize(1);
  }

  @Test
  void
      given_OneInConnectorWithOneRoute_when_ConnectorIsRegistered_then_RouteIdIsUseCasePlusConnectorName()
          throws Exception {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:routeIdTest");
    routerSubject.input(inConnector);
    routeStarter.buildRoutes(routerSubject.toCentralRouter());

    String expectedRouteId = format("%s-%s", routerSubject.getScenario(), inConnector.getName());
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
    assertThat(routerSubject.isConfigured).isTrue();
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
    routerSubject.input(inConnector).parallelOutput(parallelOut1, parallelOut2);
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
