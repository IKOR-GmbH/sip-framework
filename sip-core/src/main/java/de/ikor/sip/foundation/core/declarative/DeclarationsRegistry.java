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
public class DeclarationsRegistry implements DeclarationRegistryApi {

  private static final String CONNECTOR = "connector";
  private static final String SCENARIO = "integration scenario";
  private static final String ENDPOINT = "endpoint";

  private final List<ConnectorDefinition> connectors;
  private final List<IntegrationScenarioDefinition> scenarios;
  private final List<EndpointDefinition> endpoints;

  public DeclarationsRegistry(
      List<ConnectorDefinition> connectors,
      List<IntegrationScenarioDefinition> scenarios,
      List<EndpointDefinition> endpoints) {
    this.connectors = connectors;
    this.scenarios = scenarios;
    this.endpoints = endpoints;

    createMissingConnectors();
    checkForDuplicateConnectors();
    checkForDuplicateScenarios();
    checkForUnusedScenarios();
    checkForDuplicateEndpoints();
  }

  @Override
  public Optional<ConnectorDefinition> getConnectorById(final String connectorId) {
    return connectors.stream()
        .filter(connector -> connector.getID().equals(connectorId))
        .findFirst();
  }

  @Override
  public IntegrationScenarioDefinition getScenarioById(final String scenarioId) {
    return scenarios.stream()
        .filter(scenario -> scenario.getID().equals(scenarioId))
        .findFirst()
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("There is no integration scenario with id: %s", scenarioId)));
  }

  @Override
  public Optional<EndpointDefinition> getEndpointById(final String endpointId) {
    return endpoints.stream()
        .filter(endpoint -> endpoint.getEndpointId().equals(endpointId))
        .findFirst();
  }

  @Override
  public List<InboundEndpointDefinition> getInboundEndpoints() {
    return endpoints.stream()
        .filter(InboundEndpointDefinition.class::isInstance)
        .map(InboundEndpointDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public List<OutboundEndpointDefinition> getOutboundEndpoints() {
    return endpoints.stream()
        .filter(OutboundEndpointDefinition.class::isInstance)
        .map(OutboundEndpointDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<InboundEndpointDefinition> getInboundEndpointById(String endpointId) {
    return endpoints.stream()
        .filter(endpoint -> endpoint.getEndpointId().equals(endpointId))
        .map(InboundEndpointDefinition.class::cast)
        .findFirst();
  }

  @Override
  public Optional<OutboundEndpointDefinition> getOutboundEndpointById(String endpointId) {
    return endpoints.stream()
        .filter(endpoint -> endpoint.getEndpointId().equals(endpointId))
        .map(OutboundEndpointDefinition.class::cast)
        .findFirst();
  }

  @Override
  public List<InboundEndpointDefinition> getInboundEndpointsByConnectorId(String connectorId) {
    return endpoints.stream()
        .filter(endpoint -> endpoint.getConnectorId().equals(connectorId))
        .filter(InboundEndpointDefinition.class::isInstance)
        .map(InboundEndpointDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public List<OutboundEndpointDefinition> getOutboundEndpointsByConnectorId(String connectorId) {
    return endpoints.stream()
        .filter(endpoint -> endpoint.getConnectorId().equals(connectorId))
        .filter(OutboundEndpointDefinition.class::isInstance)
        .map(OutboundEndpointDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public List<InboundEndpointDefinition> getInboundEndpointsByScenarioId(String scenarioId) {
    return endpoints.stream()
        .filter(endpoint -> endpoint.getScenarioId().equals(scenarioId))
        .filter(InboundEndpointDefinition.class::isInstance)
        .map(InboundEndpointDefinition.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public List<OutboundEndpointDefinition> getOutboundEndpointsByScenarioId(String scenarioId) {
    return endpoints.stream()
        .filter(endpoint -> endpoint.getScenarioId().equals(scenarioId))
        .filter(OutboundEndpointDefinition.class::isInstance)
        .map(OutboundEndpointDefinition.class::cast)
        .collect(Collectors.toList());
  }

  private void createMissingConnectors() {
    endpoints.forEach(
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

  private void checkForUnusedScenarios() {
    scenarios.stream()
        .filter(scenario -> getInboundEndpointsByScenarioId(scenario.getID()).isEmpty())
        .filter(scenario -> getOutboundEndpointsByScenarioId(scenario.getID()).isEmpty())
        .map(
            scenario -> {
              throw new SIPFrameworkInitializationException(
                  String.format(
                      "There is unused integration scenario with id %s", scenario.getID()));
            })
        .collect(Collectors.toList());
  }

  private void checkForDuplicateEndpoints() {
    Set<String> set = new HashSet<>();
    List<String> endpointIds =
        endpoints.stream().map(EndpointDefinition::getEndpointId).collect(Collectors.toList());
    endpointIds.forEach(id -> checkIfDuplicate(set, id, ENDPOINT));
  }

  private void checkIfDuplicate(Set<String> set, String id, String declarativeElement) {
    if (!set.add(id)) {
      throw new SIPFrameworkInitializationException(
          String.format("There is a duplicate %s id: %s", declarativeElement, id));
    }
  }
}
