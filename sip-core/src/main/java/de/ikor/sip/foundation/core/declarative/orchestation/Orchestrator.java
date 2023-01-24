package de.ikor.sip.foundation.core.declarative.orchestation;

public interface Orchestrator<T_ORCHESTRATION_INFO extends OrchestrationInfo> {

  boolean canOrchestrate(T_ORCHESTRATION_INFO data);

  void doOrchestrate(T_ORCHESTRATION_INFO data);

  default void doBridge(T_ORCHESTRATION_INFO data) {}
}
