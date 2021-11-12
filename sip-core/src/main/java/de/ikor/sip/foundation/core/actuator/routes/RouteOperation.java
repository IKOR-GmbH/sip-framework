package de.ikor.sip.foundation.core.actuator.routes;

import de.ikor.sip.foundation.core.actuator.common.IntegrationManagementException;
import java.util.Arrays;
import java.util.Optional;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;

enum RouteOperation {
  START("start", RouteController::startRoute),
  STOP("stop", RouteController::stopRoute),
  SUSPEND("suspend", RouteController::suspendRoute),
  RESUME("resume", RouteController::resumeRoute);

  RouteOperation(String operationId, CheckedBiConsumer<RouteController, String> routeIdConsumer) {
    this.operationId = operationId;
    this.routeConsumer = routeIdConsumer;
  }

  private final String operationId;
  private final CheckedBiConsumer<RouteController, String> routeConsumer;

  /**
   * Executes a route operation
   *
   * @param ctx CamelContext
   * @param routeId Id of the route
   */
  public void execute(CamelContext ctx, String routeId) {
    try {
      routeConsumer.consume(ctx.getRouteController(), routeId);
    } catch (Exception e) {
      throw new IntegrationManagementException(
          "Cannot execute " + name() + " for route " + routeId, e);
    }
  }

  /**
   * Get operation based on its id
   *
   * @param operationId id of operation
   * @return {@link RouteOperation}
   */
  public static RouteOperation fromId(String operationId) {
    Optional<RouteOperation> rop =
        Arrays.stream(RouteOperation.values())
            .filter(op -> op.operationId.equals(operationId))
            .findFirst();
    return rop.orElseThrow(
        () -> new IncompatibleOperationException("Invalid operation id: " + operationId));
  }
}
