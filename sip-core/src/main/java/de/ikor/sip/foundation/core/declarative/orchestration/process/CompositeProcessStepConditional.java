package de.ikor.sip.foundation.core.declarative.orchestration.process;

@FunctionalInterface
public interface CompositeProcessStepConditional {

  boolean determineCondition(CompositeProcessOrchestrationContext context);
}
