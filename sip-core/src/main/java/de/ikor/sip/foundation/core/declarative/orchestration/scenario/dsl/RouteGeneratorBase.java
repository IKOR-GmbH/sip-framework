package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.model.RoutesDefinition;

/**
 * Base class for generating Camel routes from a DSL
 *
 * <p><em>For internal use only</em>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class RouteGeneratorBase {
  @Getter(AccessLevel.PROTECTED)
  private final ScenarioOrchestrationInfo orchestrationInfo;

  @Getter(value = AccessLevel.PROTECTED, lazy = true)
  private final RoutesDefinition routesDefinition = orchestrationInfo.getRoutesDefinition();

  @Getter(value = AccessLevel.PROTECTED, lazy = true)
  private final DeclarationsRegistryApi declarationsRegistry =
      getRoutesDefinition()
          .getCamelContext()
          .getRegistry()
          .findSingleByType(DeclarationsRegistryApi.class);

  @Getter(value = AccessLevel.PROTECTED, lazy = true)
  private final RoutesRegistry routesRegistry =
      getRoutesDefinition().getCamelContext().getRegistry().findSingleByType(RoutesRegistry.class);

  protected String getIntegrationScenarioId() {
    return getIntegrationScenario().getId();
  }

  protected IntegrationScenarioDefinition getIntegrationScenario() {
    return getOrchestrationInfo().getIntegrationScenario();
  }
}
