package de.ikor.sip.foundation.core.declarative.orchestation;

public interface Orchestratable<T_ORCHESTRATION_INFO extends OrchestrationInfo> {

    Orchestrator<T_ORCHESTRATION_INFO> getOrchestrator();
}
