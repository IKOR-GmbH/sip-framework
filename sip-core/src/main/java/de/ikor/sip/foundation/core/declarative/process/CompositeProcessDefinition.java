package de.ikor.sip.foundation.core.declarative.process;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestratable;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import java.util.List;

public interface CompositeProcessDefinition
    extends Orchestratable<CompositeOrchestrationInfo>,
        IntegrationScenarioProviderDefinition,
        IntegrationScenarioConsumerDefinition {

  String getId();

  String getPathToDocumentationResource();

  List<Class<? extends IntegrationScenarioDefinition>> getConsumerDefinitions();

  List<Class<? extends IntegrationScenarioDefinition>> getProviderDefinitions();
}
