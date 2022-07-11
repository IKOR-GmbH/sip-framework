package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.declarative.annotations.Connector;
import de.ikor.sip.foundation.core.declarative.annotations.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annotations.ScenarioParticipationIncoming;
import de.ikor.sip.foundation.core.declarative.annotations.ScenarioParticipationOutgoing;
import de.ikor.sip.foundation.core.declarative.definitions.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.definitions.IntegrationScenarioDefinition;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@RestControllerEndpoint(id = "adapterdefinition")
public class AdapterDefinitionEndpoint {

  private final ApplicationContext context;

  private final Map<String, ConnectorInfo> connectorInfos = new HashMap<>();
  private final Map<String, IntegrationScenarioInfo> integrationScenarios = new HashMap<>();

  public AdapterDefinitionEndpoint(final ApplicationContext context) {
    this.context = context;
  }

  @PostConstruct
  private void buildRegistry() {
    var connectors =
        context.getBeansWithAnnotation(Connector.class).values().stream()
            .map(ConnectorDefinition.class::cast)
            .collect(Collectors.toUnmodifiableList());
    for (var connector : connectors) {
      var incomingParticipations =
          Arrays.stream(connector.getClass().getMethods())
              .filter(method -> method.isAnnotationPresent(ScenarioParticipationIncoming.class))
              .map(method -> method.getAnnotation(ScenarioParticipationIncoming.class))
              .map(ScenarioParticipationIncoming::value)
              .collect(Collectors.toUnmodifiableList());
      var outgoingParticipations =
          Arrays.stream(connector.getClass().getMethods())
              .filter(method -> method.isAnnotationPresent(ScenarioParticipationOutgoing.class))
              .map(method -> method.getAnnotation(ScenarioParticipationOutgoing.class))
              .map(ScenarioParticipationOutgoing::value)
              .collect(Collectors.toUnmodifiableList());
      var info =
          new ConnectorInfo.ConnectorInfoBuilder()
              .connectorId(connector.getID())
              .connectorDescription(connector.getDocumentation())
              .participatesIncoming(incomingParticipations)
              .participatesOutgoing(outgoingParticipations)
              .build();
      connectorInfos.put(connector.getID(), info);
    }

    var scenarios =
        context.getBeansWithAnnotation(IntegrationScenario.class).values().stream()
            .map(IntegrationScenarioDefinition.class::cast)
            .collect(Collectors.toUnmodifiableList());
    for (var scenario : scenarios) {
      var info =
          new IntegrationScenarioInfo.IntegrationScenarioInfoBuilder()
              .scenarioId(scenario.getID())
              .scenarioDescription(scenario.getDescription())
              .domainModelClass(scenario.getDomainModelBaseClass().getName())
              .build();
      integrationScenarios.put(info.getScenarioId(), info);
    }
  }

  @GetMapping("/connectors")
  public List<ConnectorInfo> retrieveConnectorInfos() {
    return connectorInfos.values().stream().collect(Collectors.toUnmodifiableList());
  }

  @GetMapping("/scenarios")
  public List<IntegrationScenarioInfo> retrieveScenarioInfos() {
    return integrationScenarios.values().stream().collect(Collectors.toUnmodifiableList());
  }
}
