package de.ikor.sip.foundation.core.declarative.model;

public interface ModelMapper<C, S> {

  String CONNECTOR_TO_SCENARIO_METHOD_NAME = "mapConnectorToScenarioModel";
  String SCENARIO_TO_CONNECTOR_METHOD_NAME = "mapScenarioToConnectorModel";

  S mapConnectorToScenarioModel(C connectorModel);

  C mapScenarioToConnectorModel(S scenarioModel);

  Class<S> getScenarioModelClass();

  Class<C> getConnectorModelClass();
}
