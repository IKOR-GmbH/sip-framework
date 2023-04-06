package de.ikor.sip.foundation.core.declarative.orchestration.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import java.util.List;

public class AutoMagicScenarioOrchestrator implements Orchestrator<ScenarioOrchestrationInfo> {

  private final List<StandardScenarioOrchestrators> automaticCandidates;
  private Orchestrator<ScenarioOrchestrationInfo> matchingOrchestrator;

  public AutoMagicScenarioOrchestrator(StandardScenarioOrchestrators... automaticCandidates) {
    this.automaticCandidates = List.of(automaticCandidates);
  }

  @Override
  public boolean canOrchestrate(final ScenarioOrchestrationInfo data) {
    if (matchingOrchestrator == null) {
      matchingOrchestrator = findMatchOrThrow(data);
    }
    return matchingOrchestrator.canOrchestrate(data);
  }

  private Orchestrator<ScenarioOrchestrationInfo> findMatchOrThrow(
      final ScenarioOrchestrationInfo data) {
    return automaticCandidates.stream()
        .map(StandardScenarioOrchestrators::get)
        .filter(orchestrator -> orchestrator.canOrchestrate(data))
        .findFirst()
        .orElseThrow(
            () ->
                new SIPFrameworkException(
                    String.format(
                        "Unable to automatically orchestrate scenario '%s'. Please specify an orchestrator.",
                        data.getIntegrationScenario().getId())));
  }

  @Override
  public void doOrchestrate(final ScenarioOrchestrationInfo data) {
    if (matchingOrchestrator == null) {
      matchingOrchestrator = findMatchOrThrow(data);
    }
    matchingOrchestrator.doOrchestrate(data);
  }
}
