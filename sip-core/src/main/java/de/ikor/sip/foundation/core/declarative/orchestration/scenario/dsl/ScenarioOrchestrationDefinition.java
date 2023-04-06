package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;

public class ScenarioOrchestrationDefinition<M>
    extends ScenarioDslDefinitionBase<ScenarioOrchestrationDefinition<M>, Void, M> {

  @Getter(AccessLevel.PACKAGE)
  private final List<ForScenarioProvidersBaseDefinition<?, ?, M>> scenarioProviderDefinitions =
      new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private boolean catchAllAdded = false;

  public ScenarioOrchestrationDefinition(final IntegrationScenarioDefinition integrationScenario) {
    super(null, integrationScenario);
  }

  public <T extends InboundConnectorDefinition<?>>
      ForScenarioProvidersWithClassDefinition<ScenarioOrchestrationDefinition<M>, M>
          forInboundConnectors(final Class<T>... connectorClass) {
    return forScenarioProviders(connectorClass);
  }

  public <T extends IntegrationScenarioProviderDefinition>
      ForScenarioProvidersWithClassDefinition<ScenarioOrchestrationDefinition<M>, M>
          forScenarioProviders(final Class<T>... providerClass) {
    verifyNoCatchAllOrThrow();
    final ForScenarioProvidersWithClassDefinition<ScenarioOrchestrationDefinition<M>, M> def =
        new ForScenarioProvidersWithClassDefinition<>(
            self(),
            getIntegrationScenario(),
            Arrays.stream(providerClass).collect(Collectors.toSet()));
    scenarioProviderDefinitions.add(def);
    return def;
  }

  private void verifyNoCatchAllOrThrow() {
    if (catchAllAdded) {
      throw new SIPFrameworkException(
          "Catch-all definition already added via method forAnyUnspecifiedProvider(). No further definitions allowed.");
    }
  }

  public ForScenarioProvidersWithConnectorIdDefinition<ScenarioOrchestrationDefinition<M>, M>
      forInboundConnectors(final String... inboundConnectorId) {
    verifyNoCatchAllOrThrow();
    final ForScenarioProvidersWithConnectorIdDefinition<ScenarioOrchestrationDefinition<M>, M> def =
        new ForScenarioProvidersWithConnectorIdDefinition<>(
            self(),
            getIntegrationScenario(),
            Arrays.stream(inboundConnectorId).collect(Collectors.toSet()));
    scenarioProviderDefinitions.add(def);
    return def;
  }

  public ForScenarioProvidersCatchAllDefinition<ScenarioOrchestrationDefinition<M>, M>
      forAnyUnspecifiedProvider() {
    verifyNoCatchAllOrThrow();
    catchAllAdded = true;
    final ForScenarioProvidersCatchAllDefinition<ScenarioOrchestrationDefinition<M>, M> def =
        new ForScenarioProvidersCatchAllDefinition<>(self(), getIntegrationScenario());
    scenarioProviderDefinitions.add(def);
    return def;
  }
}
