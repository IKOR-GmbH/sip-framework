package de.ikor.sip.foundation.core.declarative.orchestration.common.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestratable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Class managing the context of an orchestration process for orchestration-rules defined via
 * orchestration DSL.
 *
 * @param <O> {@link Orchestratable} that is being orchestrated in this context (e.g. an integration
 *     scenario)
 * @param <S> Type of the steps that are being executed in this orchestration process (e.g. outbound
 *     connectors)
 */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class OrchestrationContext<O extends Orchestratable<?>, S> {

  @Getter private final O orchestratedElement;
  private final Object originalRequest;
  private final List<OrchestrationStepResponse<S, ?>> orchestrationStepResponses =
      Collections.synchronizedList(new ArrayList<>());
  private Optional<Object> aggregatedResponse = Optional.empty();

  public <T> T getOriginalRequest() {
    return (T) originalRequest;
  }

  public <T> Optional<T> getAggregatedResponse() {
    return (Optional<T>) aggregatedResponse;
  }

  public <T> void setAggregatedResponse(final T response) {
    this.aggregatedResponse = Optional.of(response);
  }

  public <T> Optional<OrchestrationStepResponse<S, T>> getResponseForLatestStep() {
    return orchestrationStepResponses.isEmpty()
        ? Optional.empty()
        : Optional.of(
            (OrchestrationStepResponse<S, T>)
                orchestrationStepResponses.get(orchestrationStepResponses.size() - 1));
  }

  protected <R> void addResponseForStep(
      final S step, final R response, final Optional<StepResultCloner<R>> cloner) {
    orchestrationStepResponses.add(
        new OrchestrationStepResponse<>(
            step, cloner.isPresent() ? cloner.get().apply(response) : response));
  }

  public record OrchestrationStepResponse<S, R>(S orchestrationStep, R result) {}
}
