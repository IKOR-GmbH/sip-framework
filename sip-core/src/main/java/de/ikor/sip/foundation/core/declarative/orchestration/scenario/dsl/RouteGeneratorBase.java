package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistryApi;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.model.RoutesDefinition;

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
          .lookupByNameAndType(DeclarationsRegistryApi.BEAN_NAME, DeclarationsRegistryApi.class);
}
