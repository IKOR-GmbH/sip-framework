package de.ikor.sip.foundation.core.actuator.routes;

import de.ikor.sip.foundation.core.actuator.routes.annotations.RouteIdParameter;
import de.ikor.sip.foundation.core.actuator.routes.annotations.RouteOperationParameter;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.api.management.ManagedCamelContext;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Entry point of the HTTP-only Actuator endpoint that exposes management functions from the
 * CamelContext and Camel's JMX MBeans.
 *
 * <p>Among other features, you can use it to list, start and stop Camel routes, as well as to get a
 * plenty of details about each one of them.
 */
@Component
@RestControllerEndpoint(id = "adapter-routes")
public class AdapterRouteEndpoint {
  private final CamelContext camelContext;
  private final RouteControllerLoggingDecorator routeController;
  private final ManagedCamelContext mbeanContext;

  /**
   * Route endpoint
   *
   * @param camelContext - CamelContext
   */
  public AdapterRouteEndpoint(
      CamelContext camelContext, RouteControllerLoggingDecorator routeController) {
    this.camelContext = camelContext;
    this.routeController = routeController;
    this.mbeanContext = camelContext.getExtension(ManagedCamelContext.class);
  }

  /**
   * List of routes summaries
   *
   * @return AdapterRouteSummary
   */
  @GetMapping
  @Operation(summary = "Get all routes", description = "Get list of Routes from Camel Context")
  public List<AdapterRouteSummary> routes() {
    return camelContext.getRoutes().stream()
        .map(route -> new AdapterRouteSummary(mbeanContext.getManagedRoute(route.getRouteId())))
        .collect(Collectors.toList());
  }

  /** Stops all routes */
  @PostMapping("/stop")
  @Operation(summary = "Stop all routes", description = "Stops all routes in Camel Context")
  public void stopAll() {
    camelContext
        .getRoutes()
        .forEach(route -> RouteOperation.STOP.execute(routeController, route.getRouteId()));
  }

  /** Resumes all routes */
  @PostMapping("/resume")
  @Operation(summary = "Resume all routes", description = "Resumes all routes in Camel Context")
  public void resumeAll() {
    camelContext
        .getRoutes()
        .forEach(route -> RouteOperation.RESUME.execute(routeController, route.getRouteId()));
  }
  /** Suspends all routes */
  @PostMapping("/suspend")
  @Operation(summary = "Suspend all routes", description = "Suspends all routes in Camel Context")
  public void suspendAll() {
    camelContext
        .getRoutes()
        .forEach(route -> RouteOperation.SUSPEND.execute(routeController, route.getRouteId()));
  }

  /** Starts all routes */
  @SneakyThrows
  @PostMapping("/start")
  @Operation(summary = "Start all routes", description = "Starts all routes in Camel Context")
  public void startAll() {
    camelContext.getRouteController().startAllRoutes();
  }

  /**
   * Returns details of a route
   *
   * @param routeId - PathVariable
   * @return AdapterRouteDetails
   */
  @GetMapping("/{routeId}")
  @Operation(summary = "Get route details", description = "Get route details")
  public AdapterRouteDetails route(@RouteIdParameter @PathVariable String routeId) {
    return new AdapterRouteDetails(mbeanContext.getManagedRoute(routeId));
  }

  /**
   * Executes a route operation
   *
   * @param routeId - PathVariable
   * @param operation - RouteOperation
   */
  @PostMapping("/{routeId}/{operation}")
  @Operation(summary = "Execute operation", description = "Executes operation on route")
  public void execute(
      @RouteIdParameter @PathVariable String routeId,
      @RouteOperationParameter @PathVariable String operation) {
    RouteOperation routeOperation = RouteOperation.fromId(operation);
    routeOperation.execute(routeController, routeId);
  }

  /** Resets all routes */
  @PostMapping("/reset")
  @Operation(summary = "Reset all routes", description = "Resets all routes in Camel Context")
  public void resetAll() {
    camelContext
        .getRoutes()
        .forEach(route -> mbeanContext.getManagedRoute(route.getRouteId()).reset());
  }

  /**
   * Reset a specific route
   *
   * @param routeId - PathVariable
   */
  @PostMapping("/{routeId}/reset")
  @Operation(summary = "Reset route", description = "Reset route")
  public void resetStatistics(@RouteIdParameter @PathVariable String routeId) {
    mbeanContext.getManagedRoute(routeId).reset();
  }

  /**
   * Executes an operation on SipMc route
   *
   * @param operation - RouteOperation
   */
  @PostMapping("/sipmc/{operation}")
  @Operation(
      summary = "Execute operation on sipmc",
      description =
          "Execute operation on all routes which use consumer from SIP Middle component (sipmc)")
  public void executeOnSipmcRoute(@RouteOperationParameter @PathVariable String operation) {
    Stream<Route> sipMcRoutes = filterMiddleComponentProducerRoutes(this.camelContext.getRoutes());
    sipMcRoutes.forEach(route -> this.execute(route.getRouteId(), operation));
  }

  /** Executes reset operation on SipMc route */
  @PostMapping("/sipmc/reset")
  @Operation(
      summary = "Reset sipmc routes",
      description = "Reset all routes which use consumer from SIP Middle component (sipmc)")
  public void resetSipmcRoute() {
    Stream<Route> sipMcRoutes = filterMiddleComponentProducerRoutes(this.camelContext.getRoutes());
    sipMcRoutes.forEach(route -> mbeanContext.getManagedRoute(route.getRouteId()).reset());
  }

  /**
   * Returns a list of "sipmc" routes
   *
   * @param routes Active routes with sipmc consumers
   * @return Stream<Route>
   */
  private Stream<Route> filterMiddleComponentProducerRoutes(List<Route> routes) {

    return routes.stream()
        .filter(route -> route.getEndpoint().getEndpointUri().startsWith("sipmc"));
  }

  /**
   * List of "sipmc" routes summaries
   *
   * @return AdapterRouteSummary
   */
  @GetMapping("/sipmc")
  @Operation(
      summary = "Get sipmc route summary",
      description = "Get summaries of routes which use consumer from SIP Middle component (sipmc)")
  public List<AdapterRouteSummary> sipmcRoutes() {
    Stream<Route> routeStream = filterMiddleComponentProducerRoutes(camelContext.getRoutes());

    Stream<AdapterRouteSummary> adapterRouteSummaryStream =
        routeStream.map(
            route -> new AdapterRouteSummary(mbeanContext.getManagedRoute(route.getRouteId())));

    return adapterRouteSummaryStream.collect(Collectors.toList());
  }
}
