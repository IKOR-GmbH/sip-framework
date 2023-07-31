package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl.ScenarioStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;

/** DSL class for calling a scenario consumer specified by it's class */
public final class CallProcessConsumer<R, M>
    extends ProcessDslBase<CallProcessConsumer<R, M>, R, M> {

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioDefinition> consumerClass;

  @Getter(AccessLevel.PACKAGE)
  private Optional<CompositeProcessStepRequestExtractor<M>> requestPreparation = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<CompositeProcessStepResponseConsumer<M>> responseConsumer = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<StepResultCloner<M>> stepResultCloner = Optional.empty();

  CallProcessConsumer(
      final R dslReturnDefinition,
      final CompositeProcessDefinition compositeProcess,
      final Class<? extends IntegrationScenarioDefinition> consumerClass) {
    super(dslReturnDefinition, compositeProcess);
    this.consumerClass = consumerClass;
  }

  /**
   * Attaches a {@link ScenarioStepRequestExtractor} that allows to manipulate the request for this
   * call. The request is only modified for this consumer and does not affect any subsequent
   * consumer calls, which will receive the original request by default.
   *
   * @param requestPreparation the extractor for the request
   * @return DSL handle
   */
  public CallProcessConsumer<R, M> withRequestPreparation(
      final CompositeProcessStepRequestExtractor<M> requestPreparation) {
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
  public R andHandleResponse(final CompositeProcessStepResponseConsumer<M> responseConsumer) {
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
