package de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl;

import de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl.ScenarioOrder.Then;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

public class ScenarioOrderDSLFrom implements ScenarioOrder.From {

  ScenarioOrderDSL scenarioOrderDSL;

  public ScenarioOrderDSLFrom(ScenarioOrderDSL scenarioOrderDSL) {
    this.scenarioOrderDSL = scenarioOrderDSL;
  }

  @Override
  public Then first(String connectorId) {
    scenarioOrderDSL.orderedConnectors = new ArrayList<>();
    scenarioOrderDSL.orderedConnectors.add(connectorId);
    return new ScenarioOrderDSLThen(scenarioOrderDSL);
  }

  @Override
  public ScenarioOrderDefinition aggregate(Function<Map<String, Object>, Object> method) {
    scenarioOrderDSL.method = method;
    return ScenarioOrderDefinition.builder()
        .fromConnectorID(scenarioOrderDSL.getFromConnectorID())
        .fromAll(scenarioOrderDSL.getFromAll())
        .orderedConectors(scenarioOrderDSL.getOrderedConnectors())
        .method(scenarioOrderDSL.getMethod())
        .build();
  }
}
