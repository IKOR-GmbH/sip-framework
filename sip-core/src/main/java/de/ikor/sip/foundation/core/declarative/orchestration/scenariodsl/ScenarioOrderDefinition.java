package de.ikor.sip.foundation.core.declarative.orchestration.scenariodsl;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ScenarioOrderDefinition {
  String fromConnectorID;
  Boolean fromAll;
  ArrayList<String> orderedConectors;

  Function<Map<String, Object>, Object> method;
}
