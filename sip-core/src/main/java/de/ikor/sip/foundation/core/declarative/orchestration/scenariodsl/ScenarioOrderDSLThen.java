package de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl;

import de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl.ScenarioOrder.Then;
import de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl.ScenarioOrder.ThenOthers;

public class ScenarioOrderDSLThen implements ScenarioOrder.Then {

  ScenarioOrderDSL scenarioOrderDSL;

  public ScenarioOrderDSLThen(ScenarioOrderDSL scenarioOrderDSL) {
    this.scenarioOrderDSL = scenarioOrderDSL;
  }

  @Override
  public Then then(String connectorID) {
    this.scenarioOrderDSL.orderedConnectors.add(connectorID);
    return this;
  }

  @Override
  public ThenOthers thenOthers() {
    return new ScenarioOrderDSLThenOthers(scenarioOrderDSL);
  }
}
