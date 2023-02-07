package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connectors.DefaultConnector;
import de.ikor.sip.foundation.core.declarative.endpoints.*;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Getter
@Slf4j
@Service
public class DeclarationsRegistry {

  private static final String CONNECTOR = "connector";
  private static final String SCENARIO = "integration scenario";
  private static final String ENDPOINT = "endpoint";

  private final List<ConnectorDefinition> connectors;
  private final List<IntegrationScenarioDefinition> scenarios;
  private final List<InboundEndpointDefinition> inboundEndpoints;
  private final List<OutboundEndpointDefinition> outboundEndpoints;

  public DeclarationsRegistry(
      List<ConnectorDefinition> connectors,
      List<IntegrationScenarioDefinition> scenarios,
      List<InboundEndpointDefinition> inboundEndpoints,
      List<OutboundEndpointDefinition> outboundEndpoints) {
    this.connectors = connectors;
    this.scenarios = scenarios;
    this.inboundEndpoints = inboundEndpoints;
    this.outboundEndpoints = outboundEndpoints;

    createMissingConnectors();
    checkForDuplicateConnectors();
    checkForDuplicateScenarios();
    checkForDuplicateEndpoints();
  }

  public Optional<ConnectorDefinition> getConnectorById(final String connectorId) {
    return connectors.stream()
        .filter(connector -> connector.getID().equals(connectorId))
        .findFirst();
  }

  public IntegrationScenarioDefinition getScenarioById(final String scenarioId) {
    return scenarios.stream()
        .filter(scenario -> scenario.getID().equals(scenarioId))
        .findFirst()
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("There is no integration scenario with id: %s", scenarioId)));
  }

  public List<InboundEndpointDefinition> getInboundEndpointsByConnectorId(String connectorId) {
    return inboundEndpoints.stream()
        .filter(endpoint -> endpoint.getConnectorId().equals(connectorId))
        .collect(Collectors.toList());
  }

  public List<OutboundEndpointDefinition> getOutboundEndpointsByConnectorId(String connectorId) {
    return outboundEndpoints.stream()
        .filter(endpoint -> endpoint.getConnectorId().equals(connectorId))
        .collect(Collectors.toList());
  }

  private void createMissingConnectors() {
    inboundEndpoints.forEach(
        endpoint -> {
          Optional<ConnectorDefinition> connector = getConnectorById(endpoint.getConnectorId());
          if (connector.isEmpty()) {
            connectors.add(new DefaultConnector(endpoint.getConnectorId()));
          }
        });
    outboundEndpoints.forEach(
        endpoint -> {
          Optional<ConnectorDefinition> connector = getConnectorById(endpoint.getConnectorId());
          if (connector.isEmpty()) {
            connectors.add(new DefaultConnector(endpoint.getConnectorId()));
          }
        });
  }

  private void checkForDuplicateConnectors() {
    Set<String> set = new HashSet<>();
    List<String> connectorIds =
        connectors.stream().map(ConnectorDefinition::getID).collect(Collectors.toList());
    connectorIds.forEach(id -> checkIfDuplicate(set, id, CONNECTOR));
  }

  private void checkForDuplicateScenarios() {
    Set<String> set = new HashSet<>();
    List<String> scenarioIds =
        scenarios.stream().map(IntegrationScenarioDefinition::getID).collect(Collectors.toList());
    scenarioIds.forEach(id -> checkIfDuplicate(set, id, SCENARIO));
  }

  private void checkForDuplicateEndpoints() {
    Set<String> set = new HashSet<>();
    List<String> inboundIds =
        inboundEndpoints.stream()
            .map(endpoint -> ((AnnotatedInboundEndpoint) endpoint).getEndpointId())
            .collect(Collectors.toList());
    inboundIds.forEach(id -> checkIfDuplicate(set, id, ENDPOINT));
    List<String> outboundIds =
        outboundEndpoints.stream()
            .map(endpoint -> ((AnnotatedOutboundEndpoint) endpoint).getEndpointId())
            .collect(Collectors.toList());
    outboundIds.forEach(id -> checkIfDuplicate(set, id, ENDPOINT));
  }

  private void checkIfDuplicate(Set<String> set, String id, String declarativeElement) {
    if (!set.add(id)) {
      throw new SIPFrameworkInitializationException(
          String.format("There is a duplicate %s id: %s", declarativeElement, id));
    }
  }
}
