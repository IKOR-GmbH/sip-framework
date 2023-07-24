package de.ikor.sip.foundation.core.declarative.composite;

import de.ikor.sip.foundation.core.declarative.orchestration.Orchestratable;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;

public interface CompositeProcessDefinition extends Orchestratable<CompositeOrchestrationInfo> {

  String getId();

  String getPathToDocumentationResource();

  List<Class<? extends IntegrationScenarioDefinition>> getConsumerDefinitions();

  List<Class<? extends IntegrationScenarioDefinition>> getProviderDefinitions();
}
