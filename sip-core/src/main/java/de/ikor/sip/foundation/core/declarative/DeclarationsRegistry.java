package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeclarationsRegistry {

  private final ApplicationContext context;
  private Map<String, ConnectorDefinition> connectors;
  @Getter private Map<String, IntegrationScenarioDefinition> scenarios;

  @PostConstruct
  private void createRegistry() {
    this.connectors =
        context.getBeansOfType(ConnectorDefinition.class).values().stream()
            .collect(Collectors.toUnmodifiableMap(def -> def.getID(), def -> def));
    this.scenarios =
        context.getBeansOfType(IntegrationScenarioDefinition.class).values().stream()
            .collect(Collectors.toUnmodifiableMap(def -> def.getID(), def -> def));
  }

  public ConnectorDefinition getConnectorById(final String connectorId) {
    return connectors.get(connectorId);
  }

  public IntegrationScenarioDefinition getScenarioById(final String scenarioId) {
    return scenarios.get(scenarioId);
  }
}
