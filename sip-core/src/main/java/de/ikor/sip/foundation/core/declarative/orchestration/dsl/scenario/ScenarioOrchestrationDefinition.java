package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.dsl.DslDefinitionBase;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;

public class ScenarioOrchestrationDefinition
    extends DslDefinitionBase<ScenarioOrchestrationDefinition, Void> {

  @Getter(AccessLevel.PACKAGE)
  private final List<ForScenarioProvidersBaseDefinition<?, ?>> scenarioProviderDefinitions =
      new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private boolean catchAllAdded = false;

  public ScenarioOrchestrationDefinition() {
    super(null);
  }

  public <T extends InboundConnectorDefinition<?>>
      ForScenarioProvidersWithClassDefinition<ScenarioOrchestrationDefinition> forInboundConnectors(
          final Class<T>... connectorClass) {
    return forScenarioProviders(connectorClass);
  }

  public <T extends IntegrationScenarioProviderDefinition>
      ForScenarioProvidersWithClassDefinition<ScenarioOrchestrationDefinition> forScenarioProviders(
          final Class<T>... providerClass) {
    verifyNoCatchAllOrThrow();
    final var def =
        new ForScenarioProvidersWithClassDefinition<>(
            this, Arrays.stream(providerClass).collect(Collectors.toUnmodifiableSet()));
    scenarioProviderDefinitions.add(def);
    return def;
  }

  private void verifyNoCatchAllOrThrow() {
    if (catchAllAdded) {
      throw new SIPFrameworkException(
          "Catch-all definition already added via method forAllRemainingScenarioProviders. No further definitions allowed.");
    }
  }

  public ForScenarioProvidersWithConnectorIdDefinition<ScenarioOrchestrationDefinition>
      forInboundConnectors(final String... inboundConnectorId) {
    verifyNoCatchAllOrThrow();
    final var def =
        new ForScenarioProvidersWithConnectorIdDefinition<>(
            this, Arrays.stream(inboundConnectorId).collect(Collectors.toUnmodifiableSet()));
    scenarioProviderDefinitions.add(def);
    return def;
  }

  public ForScenarioProvidersCatchAllDefinition<ScenarioOrchestrationDefinition>
      forAllRemainingScenarioProviders() {
    verifyNoCatchAllOrThrow();
    catchAllAdded = true;
    final var def = new ForScenarioProvidersCatchAllDefinition<>(this);
    scenarioProviderDefinitions.add(def);
    return def;
  }
}
