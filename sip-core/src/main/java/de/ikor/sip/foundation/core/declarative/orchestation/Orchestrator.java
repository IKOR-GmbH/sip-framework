package de.ikor.sip.foundation.core.declarative.orchestation;

public interface Orchestrator<T extends OrchestrationInfo> {

  boolean canOrchestrate(T data);

  void doOrchestrate(T data);
}
