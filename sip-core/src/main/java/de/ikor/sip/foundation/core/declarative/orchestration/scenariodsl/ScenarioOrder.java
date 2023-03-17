package de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl;

import java.util.Map;
import java.util.function.Function;

public interface ScenarioOrder {
  From from(String connectorID);

  From fromAll();

  interface From extends Aggregate {

    Then first(String connectorId);
  }

  interface Then {
    Then then(String connectorID);

    ThenOthers thenOthers();
  }

  interface ThenOthers extends Aggregate {}

  interface Aggregate {
    ScenarioOrderDefinition aggregate(Function<Map<String, Object>, Object> method);
  }
}
