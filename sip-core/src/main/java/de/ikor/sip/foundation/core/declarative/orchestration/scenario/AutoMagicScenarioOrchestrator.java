package de.ikor.sip.foundation.core.declarative.orchestration.scenario;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioBase;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.List;

/**
 * Orchestrator that tries to automatically attach the first usable of a list of given orchestrators
 * for an integration scenario.
 *
 * @see IntegrationScenarioBase#getOrchestrator()
 */
public class AutoMagicScenarioOrchestrator implements Orchestrator<ScenarioOrchestrationInfo> {

  private final List<StandardScenarioOrchestrators> automaticCandidates;
  private Orchestrator<ScenarioOrchestrationInfo> matchingOrchestrator;

  public AutoMagicScenarioOrchestrator(StandardScenarioOrchestrators... automaticCandidates) {
    this.automaticCandidates = List.of(automaticCandidates);
  }

  @Override
  public boolean canOrchestrate(final ScenarioOrchestrationInfo info) {
    if (matchingOrchestrator == null) {
      matchingOrchestrator = findMatchOrThrow(info);
    }
    return matchingOrchestrator.canOrchestrate(info);
  }

  private Orchestrator<ScenarioOrchestrationInfo> findMatchOrThrow(
      final ScenarioOrchestrationInfo data) {
    return automaticCandidates.stream()
        .map(StandardScenarioOrchestrators::get)
        .filter(orchestrator -> orchestrator.canOrchestrate(data))
        .findFirst()
        .orElseThrow(
            () ->
                SIPFrameworkInitializationException.init(
                    "Unable to automatically orchestrate scenario '%s'. Please specify an orchestrator by overriding 'getOrchestrator()' method.",
                    data.getIntegrationScenario().getId()));
  }

  @Override
  public void doOrchestrate(final ScenarioOrchestrationInfo info) {
    if (matchingOrchestrator == null) {
      matchingOrchestrator = findMatchOrThrow(info);
    }
    matchingOrchestrator.doOrchestrate(info);
  }
}
