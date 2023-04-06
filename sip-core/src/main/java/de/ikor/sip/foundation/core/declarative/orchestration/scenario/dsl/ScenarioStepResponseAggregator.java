package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import java.util.Optional;

@FunctionalInterface
public interface ScenarioStepResponseAggregator<M> {

  M aggregateResponse(M latestResponse, Optional<M> previousOverallResponse);
}
