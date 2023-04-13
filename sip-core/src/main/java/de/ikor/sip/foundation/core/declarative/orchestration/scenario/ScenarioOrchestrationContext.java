package de.ikor.sip.foundation.core.declarative.orchestration.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.common.dsl.StepResultCloner;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;

@RequiredArgsConstructor
public class ScenarioOrchestrationContext<M> {

  public static final String PROPERTY_NAME = "SipScenarioOrchestrationContext";

  @Getter private final IntegrationScenarioDefinition integrationScenario;
  private final Object originalRequest;
  private final List<OrchestrationStepResponse<M>> orchestrationStepResponses =
      Collections.synchronizedList(new ArrayList<>());
  private M aggregatedResponse;

  public <T> T getOriginalRequest() {
    return (T) originalRequest;
  }

  @Synchronized
  public Optional<M> getAggregatedResponse() {
    return Optional.ofNullable(aggregatedResponse);
  }

  @Synchronized
  public M setAggregatedResponse(final M response, final Optional<StepResultCloner<M>> cloner) {
    aggregatedResponse = cloner.map(c -> c.apply(response)).orElse(response);
    return aggregatedResponse;
  }

  @Synchronized
  public Optional<OrchestrationStepResponse<M>> getResponseForLatestStep() {
    return orchestrationStepResponses.isEmpty()
        ? Optional.empty()
        : Optional.of(orchestrationStepResponses.get(orchestrationStepResponses.size() - 1));
  }

  @Synchronized
  public M addResponseForStep(
      final IntegrationScenarioConsumerDefinition step,
      final M response,
      final Optional<StepResultCloner<M>> cloner) {
    final M maybeClonedResponse = cloner.map(c -> c.apply(response)).orElse(response);
    orchestrationStepResponses.add(new OrchestrationStepResponse<>(step, maybeClonedResponse));
    return maybeClonedResponse;
  }

  public List<OrchestrationStepResponse<M>> getOrchestrationStepResponses() {
    return Collections.unmodifiableList(orchestrationStepResponses);
  }

  public record OrchestrationStepResponse<M>(
      IntegrationScenarioConsumerDefinition orchestrationStep, M result) {}
}
