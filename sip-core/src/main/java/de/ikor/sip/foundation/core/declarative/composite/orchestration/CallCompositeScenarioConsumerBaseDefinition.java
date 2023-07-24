package de.ikor.sip.foundation.core.declarative.composite.orchestration;

import de.ikor.sip.foundation.core.declarative.composite.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioStepResponseConsumer;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;

/** DSL base class for specifying the call to an integration scenario consumer */
public abstract sealed class CallCompositeScenarioConsumerBaseDefinition<
        S extends CallCompositeScenarioConsumerBaseDefinition<S, R, M>, R, M>
    extends CompositeScenarioDslDefinitionBase<S, R, M>
    implements CompositeCallableWithinProviderDefinition
    permits CallCompositeScenarioConsumerByClassDefinition {

  @Getter(AccessLevel.PACKAGE)
  private Optional<ScenarioStepRequestExtractor<M>> requestPreparation = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<ScenarioStepResponseConsumer<M>> responseConsumer = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<StepResultCloner<M>> stepResultCloner = Optional.empty();

  CallCompositeScenarioConsumerBaseDefinition(
      final R dslReturnDefinition, final CompositeProcessDefinition integrationScenario) {
    super(dslReturnDefinition, integrationScenario);
  }

  /**
   * Attaches a {@link ScenarioStepRequestExtractor} that allows to manipulate the request for this
   * call. The request is only modified for this consumer and does not affect any subsequent
   * consumer calls, which will receive the original request by default.
   *
   * @param requestPreparation the extractor for the request
   * @return DSL handle
   */
  public S withRequestPreparation(final ScenarioStepRequestExtractor<M> requestPreparation) {
    this.requestPreparation = Optional.of(requestPreparation);
    return self();
  }

  /**
   * Attaches a {@link ScenarioStepResponseConsumer} that allows to manipulate the response of this
   * consumer call.
   *
   * <p>This is a terminal operation that will finish the definition of this consumer call.
   *
   * @param responseConsumer Consumer that handles the response
   * @return DSL handle
   */
  public R andHandleResponse(final ScenarioStepResponseConsumer<M> responseConsumer) {
    this.responseConsumer = Optional.of(responseConsumer);
    return getDslReturnDefinition();
  }

  /**
   * Declares that no specific response handling is required for this call. If the consumer provides
   * a response, it will not be modified.
   *
   * @return DSL handle
   */
  public R andNoResponseHandling() {
    return getDslReturnDefinition();
  }
}