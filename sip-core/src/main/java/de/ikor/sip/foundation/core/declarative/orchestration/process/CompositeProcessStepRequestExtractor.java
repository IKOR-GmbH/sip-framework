package de.ikor.sip.foundation.core.declarative.orchestration.process;

/**
 * Interface to extract the request object for a scenario consumer call
 *
 * @param <M> Response type of the integration call
 */
@FunctionalInterface
public interface CompositeProcessStepRequestExtractor<M> {

  /**
   * Returns the request object to be used with the consumer call
   *
   * @param context The current orchestration context
   * @return Request object
   */
  Object extractStepRequest(final CompositeProcessOrchestrationContext<M> context);
}
