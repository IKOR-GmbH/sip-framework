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

@Slf4j
@Data
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(chain = true)
public class ConnectorOrchestrator implements Orchestrator<ConnectorOrchestrationInfo> {
  private final ConnectorDefinition relatedConnector;
  private Consumer<RouteDefinition> requestRouteTransformer = this::defaultRequestTransformer;
  private Consumer<RouteDefinition> responseRouteTransformer = this::defaultResponseTransformer;

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
    data.getResponseRouteDefinition().ifPresent(responseRouteTransformer::accept);
  }
}