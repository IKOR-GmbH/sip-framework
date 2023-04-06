package de.ikor.sip.foundation.core.declarative.orchestration.connector;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import java.util.function.Consumer;
import java.util.function.Supplier;
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
 * @see ConnectorOrchestrationInfo
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Accessors(chain = true)
public class ConnectorOrchestrator implements Orchestrator<ConnectorOrchestrationInfo> {
  private final Supplier<ConnectorDefinition> relatedConnector;
  private Consumer<RouteDefinition> requestRouteTransformer = this::defaultRequestTransformer;
  private Consumer<RouteDefinition> responseRouteTransformer = this::defaultResponseTransformer;

  public static ConnectorOrchestrator forConnector(
      final Supplier<ConnectorDefinition> relatedConnector) {
    return new ConnectorOrchestrator(relatedConnector);
  }

  public static ConnectorOrchestrator forConnector(final ConnectorDefinition relatedConnector) {
    return new ConnectorOrchestrator(() -> relatedConnector);
  }

  private void defaultRequestTransformer(final RouteDefinition definition) {
    definition.log(
        LoggingLevel.WARN,
        String.format(
            "No request transformation definition has been assigned to connector '%s' in connector-class '%s'",
            relatedConnector.get().getId(), relatedConnector.getClass().getName()));
  }

  private void defaultResponseTransformer(final RouteDefinition definition) {
    definition.log(
        LoggingLevel.WARN,
        String.format(
            "No response transformation definition has been assigned to connector '%s' in connector-class '%s'",
            relatedConnector.get().getId(), relatedConnector.getClass().getName()));
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
