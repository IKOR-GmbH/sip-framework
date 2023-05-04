package de.ikor.sip.foundation.core.declarative.orchestration.scenario.dsl;

import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationContext;
import java.util.function.Predicate;

@FunctionalInterface
public interface ScenarioContextPredicate<M> extends Predicate<ScenarioOrchestrationContext<M>> {}
