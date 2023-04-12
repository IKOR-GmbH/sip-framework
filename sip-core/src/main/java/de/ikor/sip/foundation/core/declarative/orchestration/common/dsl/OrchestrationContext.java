package de.ikor.sip.foundation.core.declarative.orchestration.common.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestratable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;

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
      new ArrayList<>();
  private Optional<Object> aggregatedResponse = Optional.empty();

  public <T> T getOriginalRequest() {
    return (T) originalRequest;
  }

  @Synchronized
  public <T> Optional<T> getAggregatedResponse() {
    return (Optional<T>) aggregatedResponse;
  }

  @Synchronized
  public <T> T setAggregatedResponse(final T response, final Optional<StepResultCloner<T>> cloner) {
    final T maybeClonedResponse = cloner.map(c -> c.apply(response)).orElse(response);
    this.aggregatedResponse = Optional.of(maybeClonedResponse);
    return maybeClonedResponse;
  }

  @Synchronized
  public <T> Optional<OrchestrationStepResponse<S, T>> getResponseForLatestStep() {
    return orchestrationStepResponses.isEmpty()
        ? Optional.empty()
        : Optional.of(
            (OrchestrationStepResponse<S, T>)
                orchestrationStepResponses.get(orchestrationStepResponses.size() - 1));
  }

  @Synchronized
  public <R> R addResponseForStep(
      final S step, final R response, final Optional<StepResultCloner<R>> cloner) {
    final R maybeClonedResponse = cloner.map(c -> c.apply(response)).orElse(response);
    orchestrationStepResponses.add(new OrchestrationStepResponse<>(step, maybeClonedResponse));
    return maybeClonedResponse;
  }

  public record OrchestrationStepResponse<S, R>(S orchestrationStep, R result) {}
}
