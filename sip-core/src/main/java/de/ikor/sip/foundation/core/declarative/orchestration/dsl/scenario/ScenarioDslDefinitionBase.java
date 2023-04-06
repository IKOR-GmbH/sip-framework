package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.dsl.DslDefinitionBase;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.Getter;

/**
 * Base class for a scenario DSL definitions
 *
 * @param <S> the type of the concrete DSL definition class
 * @param <R> the type of the return definition
 * @param <M> the type of the response model
 */
public class ScenarioDslDefinitionBase<S extends ScenarioDslDefinitionBase<S, R, M>, R, M>
    extends DslDefinitionBase<S, R> {

  @Getter private final IntegrationScenarioDefinition integrationScenario;

  protected ScenarioDslDefinitionBase(
      final R dslReturnDefinition, final IntegrationScenarioDefinition integrationScenario) {
    super(dslReturnDefinition);
    this.integrationScenario = integrationScenario;
  }
}
