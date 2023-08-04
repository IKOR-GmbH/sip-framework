package de.ikor.sip.foundation.core.declarative.orchestration.process.dsl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepRequestExtractor;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeProcessStepResponseConsumer;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * DSL class for calling a process consumer specified by its class
 *
 * @param <R> DSL handle for the return DSL Verb/type.
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public final class CallProcessConsumer<R> extends ProcessDslBase<CallProcessConsumer<R>, R> {

  @Getter(AccessLevel.PACKAGE)
  private final Class<? extends IntegrationScenarioDefinition> consumerClass;

  @Getter(AccessLevel.PACKAGE)
  private Optional<CompositeProcessStepRequestExtractor> requestPreparation = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<CompositeProcessStepResponseConsumer> responseConsumer = Optional.empty();

  @Getter(AccessLevel.PACKAGE)
  private Optional<StepResultCloner<Object>> stepResultCloner = Optional.empty();

  CallProcessConsumer(
      final R dslReturnDefinition,
      final CompositeProcessDefinition compositeProcess,
      final Class<? extends IntegrationScenarioDefinition> consumerClass) {
    super(dslReturnDefinition, compositeProcess);
    this.consumerClass = consumerClass;
  }

  /**
   * Attaches a {@link CompositeProcessStepRequestExtractor} that allows to manipulate the request
   * for this call.
   *
   * @param requestPreparation the extractor for the request
   * @return DSL handle
   */
  public CallProcessConsumer<R> withRequestPreparation(
      final CompositeProcessStepRequestExtractor requestPreparation) {
    this.requestPreparation = Optional.of(requestPreparation);
    return self();
  }

  /**
   * Attaches a {@link CompositeProcessStepResponseConsumer} that allows to manipulate the response
   * of this consumer call.
   *
   * <p>This is a terminal operation that will finish the definition of this consumer call.
   *
   * @param responseConsumer Consumer that handles the response
   * @return DSL handle
   */
  public R withResponseHandling(final CompositeProcessStepResponseConsumer responseConsumer) {
    this.responseConsumer = Optional.of(responseConsumer);
    return getDslReturnDefinition();
  }

  /**
   * Declares that no specific response handling is required for this call. If the consumer provides
   * a response, it will not be modified.
   *
   * @return DSL handle
   */
  public R withNoResponseHandling() {
    return getDslReturnDefinition();
  }
}
