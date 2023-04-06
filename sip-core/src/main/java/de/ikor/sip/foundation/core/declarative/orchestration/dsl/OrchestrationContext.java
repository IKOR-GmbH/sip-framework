package de.ikor.sip.foundation.core.declarative.orchestration.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestratable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Class managing the context of an orchestration process for orchestration-rules defined via {@link
 * OrchestrationDSL}.
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
  @Getter private final Object originalRequest;
  private final Map<S, Object> orchestrationStepResponses =
      Collections.synchronizedMap(new LinkedHashMap<>());
  @Getter @Setter private boolean cloneStepResults = false;

  @SuppressWarnings("rawtypes")
  private StepResultCloner stepResultCloner = StepResultCloner.forSerializable();

  @Getter private Optional<Object> orchestrationResponse = Optional.empty();

  public <T> T getOriginalRequest() {
    return (T) originalRequest;
  }

  public <T> Optional<T> getOrchestrationResponse() {
    return (Optional<T>) orchestrationResponse;
  }

  public <T> void setOrchestrationResponse(final T orchestrationResponse) {
    this.orchestrationResponse = Optional.of(orchestrationResponse);
  }

  public <T> T getResponseForStep(final S step) {
    return (T) orchestrationStepResponses.get(step);
  }

  public <T> Optional<T> getResponseForLatestStep() {
    return (Optional<T>)
        orchestrationStepResponses.values().stream().reduce((first, second) -> second);
  }

  protected <R> void addResponseForStep(final S step, final R response) {
    orchestrationStepResponses.put(
        step, cloneStepResults ? stepResultCloner.apply(response) : response);
  }

  public <T> void setStepResultCloner(final StepResultCloner<T> stepResultCloner) {
    this.stepResultCloner = stepResultCloner;
  }
}
