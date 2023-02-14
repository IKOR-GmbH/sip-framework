package de.ikor.sip.foundation.core.declarative.orchestation;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.RouteDefinition;

/**
 * Standard implementation for orchestrating connectors.
 *
 * <p>This relies on the connector developer to implement the necessary orchestration steps for the
 * connector (such as data model transformation) using Camel's DSL from the provided {@link
 * RouteDefinition} handles.
 *
 * <p>Custom transformations should be provided using the {@link
 * #setRequestRouteTransformer(Consumer)} and {@link #setResponseRouteTransformer(Consumer)} methods
 * as necessary.
 *
 * @see ConnectorOrchestrationInfo
 * @see #forConnector(ConnectorDefinition)
 */
@Slf4j
@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(chain = true)
public class ConnectorOrchestrator implements Orchestrator<ConnectorOrchestrationInfo> {
  private final ConnectorDefinition relatedConnector;
  private Consumer<RouteDefinition> requestRouteTransformer = this::defaultRequestTransformer;
  private Consumer<RouteDefinition> responseRouteTransformer = this::defaultResponseTransformer;

  /**
   * Creates a new {@link ConnectorOrchestrator} for the given connector.
   *
   * @param connector connector to be orchestrated
   * @return new {@link ConnectorOrchestrator} instance
   */
  public static ConnectorOrchestrator forConnector(final ConnectorDefinition connector) {
    return new ConnectorOrchestrator(connector);
  }

  private void defaultRequestTransformer(final RouteDefinition definition) {
    definition.log(
        LoggingLevel.WARN,
        String.format(
            "No request transformation definition has been assigned to connector '%s' in connector-class '%s'",
            relatedConnector.getId(), relatedConnector.getClass().getName()));
  }

  private void defaultResponseTransformer(final RouteDefinition definition) {
    definition.log(
        LoggingLevel.WARN,
        String.format(
            "No response transformation definition has been assigned to connector '%s' in connector-class '%s'",
            relatedConnector.getId(), relatedConnector.getClass().getName()));
  }

  @Override
  public boolean canOrchestrate(final ConnectorOrchestrationInfo data) {
    return true;
  }

  @Override
  public void doOrchestrate(final ConnectorOrchestrationInfo data) {
    requestRouteTransformer.accept(data.getRequestRouteDefinition());
    data.getResponseRouteDefinition().ifPresent(responseRouteTransformer);
  }
}
