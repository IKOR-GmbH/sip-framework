package de.ikor.sip.foundation.core.framework.routers;

/** Exception thrown when there is Central router {@link CentralRouterDefinition} without from definition. */
public class EmptyCentralRouterException extends Exception {

  public EmptyCentralRouterException(String centralRouterScenario) {
    super(String.format("Central Router: %s has no from definition", centralRouterScenario));
  }
}
