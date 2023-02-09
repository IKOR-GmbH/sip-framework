package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connectorgroup.DefaultConnectorGroup;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Service
public class DeclarationsRegistry {

    private static final String CONNECTOR = "connector";
    private static final String SCENARIO = "integration scenario";
    private static final String ENDPOINT = "endpoint";

    private final List<ConnectorGroupDefinition> connectors;
    private final List<IntegrationScenarioDefinition> scenarios;
    private final List<InboundConnectorDefinition> inboundEndpoints;
    private final List<OutboundConnectorDefinition> outboundEndpoints;

    public DeclarationsRegistry(
            List<ConnectorGroupDefinition> connectors,
            List<IntegrationScenarioDefinition> scenarios,
            List<InboundConnectorDefinition> inboundEndpoints,
            List<OutboundConnectorDefinition> outboundEndpoints) {
        this.connectors = connectors;
        this.scenarios = scenarios;
        this.inboundEndpoints = inboundEndpoints;
        this.outboundEndpoints = outboundEndpoints;

        createMissingConnectors();
        checkForDuplicateConnectors();
        checkForDuplicateScenarios();
        checkForUnusedScenarios();
        checkForDuplicateEndpoints();
    }

    private void createMissingConnectors() {
        inboundEndpoints.forEach(
                endpoint -> {
                    Optional<ConnectorGroupDefinition> connector = getConnectorById(endpoint.getConnectorGroupId());
                    if (connector.isEmpty()) {
                        connectors.add(new DefaultConnectorGroup(endpoint.getConnectorGroupId()));
                    }
                });
        outboundEndpoints.forEach(
                endpoint -> {
                    Optional<ConnectorGroupDefinition> connector = getConnectorById(endpoint.getConnectorGroupId());
                    if (connector.isEmpty()) {
                        connectors.add(new DefaultConnectorGroup(endpoint.getConnectorGroupId()));
                    }
                });
    }

    private void checkForDuplicateConnectors() {
        Set<String> set = new HashSet<>();
        List<String> connectorIds =
                connectors.stream().map(ConnectorGroupDefinition::getID).collect(Collectors.toList());
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
                            throw new RuntimeException(
                                    String.format(
                                            "There is unused integration scenario with id %s", scenario.getID()));
                        })
                .collect(Collectors.toList());
    }

    private void checkForDuplicateEndpoints() {
        Set<String> set = new HashSet<>();
        List<String> inboundIds =
                inboundEndpoints.stream()
                        .map(endpoint -> endpoint.getConnectorId())
                        .collect(Collectors.toList());
        inboundIds.forEach(id -> checkIfDuplicate(set, id, ENDPOINT));
        List<String> outboundIds =
                outboundEndpoints.stream()
                        .map(endpoint -> endpoint.getConnectorId())
                        .collect(Collectors.toList());
        outboundIds.forEach(id -> checkIfDuplicate(set, id, ENDPOINT));
    }

    public Optional<ConnectorGroupDefinition> getConnectorById(final String connectorId) {
        return connectors.stream()
                .filter(connector -> connector.getID().equals(connectorId))
                .findFirst();
    }

    private void checkIfDuplicate(Set<String> set, String id, String declarativeElement) {
        if (!set.add(id)) {
            throw new SIPFrameworkInitializationException(
                    String.format("There is a duplicate %s id: %s", declarativeElement, id));
        }
    }

    public List<InboundConnectorDefinition> getInboundEndpointsByScenarioId(String scenarioId) {
        return inboundEndpoints.stream()
                .filter(endpoint -> endpoint.getScenarioId().equals(scenarioId))
                .collect(Collectors.toList());
    }

    public List<OutboundConnectorDefinition> getOutboundEndpointsByScenarioId(String scenarioId) {
        return outboundEndpoints.stream()
                .filter(endpoint -> endpoint.getScenarioId().equals(scenarioId))
                .collect(Collectors.toList());
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

    public List<InboundConnectorDefinition> getInboundEndpointsByConnectorId(String connectorId) {
        return inboundEndpoints.stream()
                .filter(endpoint -> endpoint.getConnectorGroupId().equals(connectorId))
                .collect(Collectors.toList());
    }

    public List<OutboundConnectorDefinition> getOutboundEndpointsByConnectorId(String connectorId) {
        return outboundEndpoints.stream()
                .filter(endpoint -> endpoint.getConnectorGroupId().equals(connectorId))
                .collect(Collectors.toList());
    }
}
