package de.ikor.sip.foundation.core.declarative.model;

public interface ModelMapper<C, S> {
  S mapConnectorToScenarioModel(C connectorModel);

  C mapScenarioToConnectorModel(S scenarioModel);

  Class<S> getScenarioModelClass();

  Class<C> getConnectorModelClass();
}
