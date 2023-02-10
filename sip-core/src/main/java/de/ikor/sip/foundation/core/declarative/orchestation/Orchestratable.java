package de.ikor.sip.foundation.core.declarative.orchestation;

public interface Orchestratable<ORCHESTRATION_TYPE extends OrchestrationInfo> {

  Orchestrator<ORCHESTRATION_TYPE> getOrchestrator();
}
