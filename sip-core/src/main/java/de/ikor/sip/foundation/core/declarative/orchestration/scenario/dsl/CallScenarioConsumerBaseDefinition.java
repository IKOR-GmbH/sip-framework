package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class CallScenarioConsumerBaseDefinition<
        S extends CallScenarioConsumerBaseDefinition<S, R, M>, R, M>
    extends ScenarioDslDefinitionBase<S, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private Optional<ScenarioStepRequestExtractor<M>> requestPreparation = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<ScenarioStepResponseConsumer<M>> responseConsumer = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<StepResultCloner<M>> stepResultCloner = Optional.empty();

  CallScenarioConsumerBaseDefinition(
      final R dslReturnDefinition, final IntegrationScenarioDefinition integrationScenario) {
    super(dslReturnDefinition, integrationScenario);
  }

  public S withRequestPreparation(final ScenarioStepRequestExtractor<M> requestPreparation) {
    this.requestPreparation = Optional.of(requestPreparation);
    return self();
  }

  public R andNoResponseHandling() {
    return getDslReturnDefinition();
  }

  public R andAggregateResponse(final ScenarioStepResponseAggregator<M> responseAggregator) {
    return andHandleResponse(
        (latestResponse, context) ->
            context.setAggregatedResponse(
                responseAggregator.aggregateResponse(
                    latestResponse, context.getAggregatedResponse())));
  }

  public R andHandleResponse(final ScenarioStepResponseConsumer<M> responseConsumer) {
    this.responseConsumer = Optional.of(responseConsumer);
    return getDslReturnDefinition();
  }

  public S withResponseCloner(final StepResultCloner<M> cloner) {
    this.stepResultCloner = Optional.of(cloner);
    return self();
  }
}
