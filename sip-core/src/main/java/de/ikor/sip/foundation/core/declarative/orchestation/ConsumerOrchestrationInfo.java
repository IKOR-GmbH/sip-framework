package de.ikor.sip.foundation.core.declarative.orchestation;

import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import java.util.List;

public interface ConsumerOrchestrationInfo extends OrchestrationInfo {

  void orchestrateTargets(List<IntegrationScenarioConsumerDefinition> consumers);
}
