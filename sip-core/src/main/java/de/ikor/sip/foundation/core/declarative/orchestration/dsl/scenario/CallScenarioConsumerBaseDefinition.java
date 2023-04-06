package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class CallScenarioConsumerBaseDefinition<
        S extends CallScenarioConsumerBaseDefinition<S, R, M>, R, M>
    extends ScenarioDslDefinitionBase<S, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private Optional<ScenarioRequestPreparation<M>> requestPreparation = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<ScenarioResponseAggregator<M>> responseAggregator = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<StepResultCloner<M>> stepResultCloner = Optional.empty();

  protected CallScenarioConsumerBaseDefinition(
      final R dslReturnDefinition, final IntegrationScenarioDefinition integrationScenario) {
    super(dslReturnDefinition, integrationScenario);
  }

  public <T> S withRequestPreparation(final ScenarioRequestPreparation<M> requestPreparation) {
    this.requestPreparation = Optional.of(requestPreparation);
    return self();
  }

  public R withNoResponseHandling() {
    return getDslReturnDefinition();
  }

  public R aggregateResponse(final ScenarioResponseAggregator<M> responseAggregator) {
    this.responseAggregator = Optional.of(responseAggregator);
    return getDslReturnDefinition();
  }

  public S withResponseCloner(final StepResultCloner<M> cloner) {
    this.stepResultCloner = Optional.of(cloner);
    return self();
  }
}
