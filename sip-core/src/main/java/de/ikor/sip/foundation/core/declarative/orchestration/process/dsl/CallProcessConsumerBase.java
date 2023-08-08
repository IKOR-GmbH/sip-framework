package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;
// eq CallScenarioConsumerBaseDefinition
/** DSL class for calling a process consumer specified by its class */
public class CallProcessConsumerBase<
        S extends CallProcessConsumerBase<S, R>, R>
        extends ProcessDslBase<S, R>
        implements CallableWithinProcessDefinition {

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioDefinition> consumerClass;

  @Getter(AccessLevel.PACKAGE)
  private Optional<CompositeProcessStepRequestExtractor> requestPreparation = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<CompositeProcessStepResponseConsumer> responseConsumer = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<StepResultCloner<Object>> stepResultCloner = Optional.empty();

  CallProcessConsumerBase(
      final R dslReturnDefinition,
      final CompositeProcessDefinition compositeProcess,
      final Class<? extends IntegrationScenarioDefinition> consumerClass) {
    super(dslReturnDefinition, compositeProcess);
    this.consumerClass = consumerClass;
  }



  public S withRequestPreparation(
      final CompositeProcessStepRequestExtractor requestPreparation) {
    this.requestPreparation = Optional.of(requestPreparation);
    return self();
  }

  public R withResponseHandling(final CompositeProcessStepResponseConsumer responseConsumer) {
    this.responseConsumer = Optional.of(responseConsumer);
    return getDslReturnDefinition();
  }

  public R withNoResponseHandling() {
    return getDslReturnDefinition();
  }
}
