package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.DslDefinitionBase;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.Getter;

/**
 * Base class for a scenario DSL definitions
 *
 * @param <S> type of the concrete DSL definition class
 * @param <R> type of the return definition
 * @param <M> type of the integration scenario's response model
 */
public abstract class ScenarioDslDefinitionBase<S extends ScenarioDslDefinitionBase<S, R, M>, R, M>
    extends DslDefinitionBase<S, R> {

  @Getter private final IntegrationScenarioDefinition integrationScenario;

  ScenarioDslDefinitionBase(
      final R dslReturnDefinition, final IntegrationScenarioDefinition integrationScenario) {
    super(dslReturnDefinition);
    this.integrationScenario = integrationScenario;
  }
}
