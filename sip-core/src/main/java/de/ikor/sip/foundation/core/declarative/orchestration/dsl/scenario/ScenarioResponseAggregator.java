package de.ikor.sip.foundation.core.declarative.orchestration.dsl.scenario;

import java.util.Optional;

@FunctionalInterface
public interface ScenarioResponseAggregator<T> {

  T mapResponseInOverallResponse(final T callResponse, final Optional<T> overallResponse);
}
