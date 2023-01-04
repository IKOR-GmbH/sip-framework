package de.ikor.sip.foundation.core.declarative.scenario;

import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import java.util.Optional;

public interface IntegrationScenarioDefinition extends Orchestratable<ConnectorOrchestrationInfo> {

  String getID();

  String getDescription();

  Class<? extends Object> getRequestModelClass();

  Optional<Class<? extends Object>> getResponseModelClass();
}
