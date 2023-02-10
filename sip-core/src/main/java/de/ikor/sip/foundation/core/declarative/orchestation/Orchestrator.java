package de.ikor.sip.foundation.core.declarative.orchestation;

public interface Orchestrator<ORCHESTRATION_TYPE extends OrchestrationInfo> {

  boolean canOrchestrate(ORCHESTRATION_TYPE data);

  void doOrchestrate(ORCHESTRATION_TYPE data);
}
