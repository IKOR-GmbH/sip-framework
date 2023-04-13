package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.EndOfDsl;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;

public class ScenarioOrchestrationDefinition<M>
    extends ScenarioDslDefinitionBase<ScenarioOrchestrationDefinition<M>, EndOfDsl, M> {

  @Getter(AccessLevel.PACKAGE)
  private final List<ForScenarioProvidersBaseDefinition<?, ?, M>> scenarioProviderDefinitions =
      new ArrayList<>();

  @Getter(AccessLevel.PACKAGE)
  private boolean catchAllAdded = false;

  public ScenarioOrchestrationDefinition(final IntegrationScenarioDefinition integrationScenario) {
    super(null, integrationScenario);
  }

  public ForScenarioProvidersWithClassDefinition<ScenarioOrchestrationDefinition<M>, M>
      forInboundConnectors(final Class<? extends InboundConnectorDefinition<?>>... connectorClass) {
    return forScenarioProviders(connectorClass);
  }

  public ForScenarioProvidersWithClassDefinition<ScenarioOrchestrationDefinition<M>, M>
      forScenarioProviders(
          final Class<? extends IntegrationScenarioProviderDefinition>... providerClass) {
    verifyNoCatchAllOrThrow();
    final ForScenarioProvidersWithClassDefinition<ScenarioOrchestrationDefinition<M>, M> def =
        new ForScenarioProvidersWithClassDefinition<>(
            self(), getIntegrationScenario(), Set.of(providerClass));
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
            self(), getIntegrationScenario(), Set.of(inboundConnectorId));
    scenarioProviderDefinitions.add(def);
    return def;
  }

  public ForScenarioProvidersCatchAllDefinition<EndOfDsl, M> forAnyUnspecifiedScenarioProvider() {
    verifyNoCatchAllOrThrow();
    catchAllAdded = true;
    final ForScenarioProvidersCatchAllDefinition<EndOfDsl, M> def =
        new ForScenarioProvidersCatchAllDefinition<>(EndOfDsl.INSTANCE, getIntegrationScenario());
    scenarioProviderDefinitions.add(def);
    return def;
  }
}
