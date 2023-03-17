package de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl;

import de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl.ScenarioOrder.ThenOthers;
import java.util.Map;
import java.util.function.Function;

public class ScenarioOrderDSLThenOthers implements ThenOthers {

  ScenarioOrderDSL scenarioOrderDSL;

  public ScenarioOrderDSLThenOthers(ScenarioOrderDSL scenarioOrderDSL) {
    this.scenarioOrderDSL = scenarioOrderDSL;
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
