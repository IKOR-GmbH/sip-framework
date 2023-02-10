package de.ikor.sip.foundation.core.declarative.orchestation;

import org.apache.camel.model.rest.RestDefinition;

public interface RestConnectorOrchestrationInfo extends OrchestrationInfo {
  RestDefinition getRestDefinition();
}
