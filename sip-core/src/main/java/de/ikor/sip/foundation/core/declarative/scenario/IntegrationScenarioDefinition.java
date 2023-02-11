package de.ikor.sip.foundation.core.declarative.scenario;

import de.ikor.sip.foundation.core.declarative.orchestation.ConsumerOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import java.util.Optional;

public interface IntegrationScenarioDefinition extends Orchestratable<ConsumerOrchestrationInfo> {

  String getID();

  Class<?> getRequestModelClass();

  Optional<Class<?>> getResponseModelClass();

  String getPathToDocumentationResource();
}
