package de.ikor.sip.foundation.core.declarative.orchestation;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;

import java.util.List;

public interface ScenarioOrchestrationInfo extends OrchestrationInfo {

    IntegrationScenarioDefinition consumesScenario();

    void orchestrateTargets(List<IntegrationScenarioDefinition> scenarios);
}
