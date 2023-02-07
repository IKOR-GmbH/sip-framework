package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.*;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Getter
@Slf4j
@Service
public class DeclarationsRegistry {

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

    checkForDuplicateEndpointIds();
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

  private void checkForDuplicateEndpointIds() {
    Set<String> set = new HashSet<>();
    List<String> inboundIds =
        inboundEndpoints.stream()
            .map(endpoint -> ((AnnotatedInboundEndpoint) endpoint).getEndpointId())
            .collect(Collectors.toList());
    inboundIds.forEach(id -> checkIfDuplicate(set, id));
    List<String> outboundIds =
        outboundEndpoints.stream()
            .map(endpoint -> ((AnnotatedOutboundEndpoint) endpoint).getEndpointId())
            .collect(Collectors.toList());
    outboundIds.forEach(id -> checkIfDuplicate(set, id));
  }

  private void checkIfDuplicate(Set<String> set, String id) {
    if (!set.add(id)) {
      // TODO: Change to SIPFrameworkInitializationException when merged with develop branch
      throw new RuntimeException(String.format("There is a duplicate endpoint id: %s", id));
    }
  }
}
