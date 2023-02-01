package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.framework.emptyapp.EmptyTestingApplication;
import de.ikor.sip.foundation.core.framework.connectors.InConnector;
import de.ikor.sip.foundation.core.framework.connectors.OutConnector;
import de.ikor.sip.foundation.core.framework.stubs.EndpointDSLInConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleInConnector;
import de.ikor.sip.foundation.core.framework.stubs.SimpleOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.StaticEndpointDSLOutConnector;
import de.ikor.sip.foundation.core.framework.stubs.routers.TestingCentralRouter;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.camel.Route;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = EmptyTestingApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CentralRouterStructureTest {
  private final TestingCentralRouter routerSubject = new TestingCentralRouter();

  @Test
  void when_SingleInConnectorIsRegistered_then_OneRouteStartingWithProperEndpointIsAvailable() {
    // arrange
    SimpleInConnector simpleInConnector = SimpleInConnector.withUri("direct:singleInConnector");
    // act
    routerSubject.input(simpleInConnector);
    routerSubject.toCentralRouter().setUpRoutes();
    // assert
    assertThat(getRoutesFromContext())
        .filteredOn(matchRoutesBasedOnUri("direct.*singleInConnector"))
        .as(
            "Test connector is not registered. Expected %s endpoint",
            simpleInConnector.getEndpointUri())
        .hasSize(1);
  }

  @Test
  void when_MultipleInConnectorsAreRegistered_then_OneRoutePerConnectorIsAvailable() {
    // arrange
    SimpleInConnector firstInConnector = SimpleInConnector.withUri("direct:sip");
    SimpleInConnector secondInConnector = SimpleInConnector.withUri("direct:sipie");
    // act
    routerSubject.input(firstInConnector, secondInConnector);
    routerSubject.toCentralRouter().setUpRoutes();
    // assert
    assertThat(getRoutesFromContext()).filteredOn(matchRoutesBasedOnUri("direct.*sip")).hasSize(1);

    assertThat(getRoutesFromContext())
        .filteredOn(matchRoutesBasedOnUri("direct.*sipie"))
        .hasSize(1);
  }

  @Test
  void
      when_OneOutConnectorIsRegisteredAsSequenced_then_OneActiveAndOneTestRouteWithSIPMCEndpointIsAvailable() {
    // arrange
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:OneOutConnector");
    SimpleOutConnector outConnector = new SimpleOutConnector();

    // act
    routerSubject.input(inConnector).sequencedOutput(outConnector);
    routerSubject.toCentralRouter().setUpRoutes();
    // assert
    assertThat(getRoutesFromContext())
        .filteredOn(matchRoutesBasedOnUri(format("sipmc.*%s", routerSubject.getScenario())))
        .as(
            "Connector reistered via sequencedOutput - Exactly one actual OutConnectorDefinition is expected.")
        .hasSize(1);
  }

  @Test
  void when_OneOutConnectorIsRegisteredAsParallel_then_OneRouteWithSIPMCEndpointIsAvailable() {
    // arrange
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:OneOutConnector");
    SimpleOutConnector outConnector = new SimpleOutConnector();

    // act
    routerSubject.input(inConnector).parallelOutput(outConnector);
    routerSubject.toCentralRouter().setUpRoutes();

    // assert
    assertThat(getRoutesFromContext())
        .filteredOn(matchRoutesBasedOnUri(format("sipmc.*%s", routerSubject.getScenario())))
        .as(
            "Connector reistered via parallelOutput - Exactly one actual OutConnectorDefinition is expected.")
        .hasSize(1);
  }

  @Test
  void
      given_OneInConnectorWithOneRoute_when_ConnectorIsRegistered_then_RouteIdIsUseCasePlusConnectorName() {
    SimpleInConnector inConnector = SimpleInConnector.withUri("direct:routeIdTest");
    routerSubject.input(inConnector);
    routerSubject.toCentralRouter().setUpRoutes();

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
  void when_CreateInEndpointWithEndpointDsl_then_VerifyRoute() {
    // arrange
    InConnector inConnector = new EndpointDSLInConnector("endpointdsl-direct", "endpointdsl-id");
    OutConnector outConnector =
        new StaticEndpointDSLOutConnector("temp/out", "staticendpointdsl-id");
    routerSubject.input(inConnector).sequencedOutput(outConnector);

    // act
    routerSubject.toCentralRouter().setUpRoutes();

    String expectedRouteId = format("%s-%s", routerSubject.getScenario(), inConnector.getName());
    Route route = getRouteFromContextById(expectedRouteId);

    // assert
    assertThat(route.getEndpoint().getEndpointUri()).isEqualTo("direct://endpointdsl-direct");
  }

  @Test
  void when_CreateMultipleOutEndpointsWithEndpointDSL_then_CheckOutEndpointsAreValid() {
    // arrange
    InConnector inConnector = new EndpointDSLInConnector("endpointdsl-direct", "endpointdsl-id");
    OutConnector outConnector1 =
        new StaticEndpointDSLOutConnector("direct1", "staticendpointdsl-id1");
    OutConnector outConnector2 =
        new StaticEndpointDSLOutConnector("direct2", "staticendpointdsl-id2");
    routerSubject.input(inConnector).sequencedOutput(outConnector1, outConnector2);

    // act
    routerSubject.toCentralRouter().setUpRoutes();

    String expectedRouteId1 = format("%s-%s", routerSubject.getScenario(), outConnector1.getName());
    String expectedRouteId2 = format("%s-%s", routerSubject.getScenario(), outConnector2.getName());
    Route route1 = getRouteFromContextById(expectedRouteId1);
    Route route2 = getRouteFromContextById(expectedRouteId2);

    // assert
    assertThat(((ToDefinition) ((RouteDefinition) route1.getRoute()).getOutputs().get(0)).getUri())
        .isEqualTo("direct://direct1");
    assertThat(((ToDefinition) ((RouteDefinition) route2.getRoute()).getOutputs().get(0)).getUri())
        .isEqualTo("direct://direct2");
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