package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.AnnotatedInboundEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.AnnotatedOutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.InboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.OutboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Service;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class DeclarationsRegistry {

  private static final String IN_PREFIX = "in";
  private static final String OUT_PREFIX = "out";
  private static final String ENDPOINT_ID_FORMAT = "%s-%s-%s";

  private CamelContext camelContext;
  private List<ConnectorDefinition> connectors;
  private List<IntegrationScenarioDefinition> integrationScenarios;
  private List<InboundEndpointDefinition> inboundEndpointDefinitions;
  private List<OutboundEndpointDefinition> outboundEndpointDefinitions;

  // TODO: some check for unique id-s of connectors and scenarios?
  @PostConstruct
  private void updateAndValidateEndpoints() {
    inboundEndpointDefinitions.forEach(
        endpoint -> initInboundEndpoint((AnnotatedInboundEndpoint) endpoint));
    outboundEndpointDefinitions.forEach(
        endpoint -> initOutboundEndpoint((AnnotatedOutboundEndpoint) endpoint));

    checkForDuplicateEndpointIds();
  }

  // TODO: This is currently never used, do we need it?
  public ConnectorDefinition getConnectorById(final String connectorId) {
    return connectors.stream()
        .filter(connector -> connector.getID().equals(connectorId))
        .findFirst()
        .orElse(null); // TODO: What to return?
  }

  public IntegrationScenarioDefinition getScenarioById(final String scenarioId) {
    return integrationScenarios.stream()
        .filter(scenario -> scenario.getID().equals(scenarioId))
        .findFirst()
        .orElseThrow(
            () ->
                new RuntimeException(
                    String.format("There is no integration scenario with id: %s", scenarioId)));
  }

  public List<InboundEndpointDefinition> getInboundEndpointsByConnectorId(String connectorId) {
    return inboundEndpointDefinitions.stream()
        .filter(endpoint -> endpoint.getConnectorId().equals(connectorId))
        .collect(Collectors.toList());
  }

  public List<OutboundEndpointDefinition> getOutboundEndpointsByConnectorId(String connectorId) {
    return outboundEndpointDefinitions.stream()
        .filter(endpoint -> endpoint.getConnectorId().equals(connectorId))
        .collect(Collectors.toList());
  }

  private void initInboundEndpoint(AnnotatedInboundEndpoint endpoint) {
    endpoint.setDeclarationsRegistry(this);
    endpoint.setCamelEndpoint(endpoint.getInboundEndpoint().resolve(camelContext));
    String annotatedEndpointId = endpoint.getAnnotationEndpointId();
    if (annotatedEndpointId.isEmpty()) {
      endpoint.setEndpointId(
          String.format(
              ENDPOINT_ID_FORMAT,
              IN_PREFIX,
              endpoint.getProvidedScenario().getID(),
              endpoint.getConnectorId()));
    } else {
      endpoint.setEndpointId(annotatedEndpointId);
    }
  }

  private void initOutboundEndpoint(AnnotatedOutboundEndpoint endpoint) {
    endpoint.setDeclarationsRegistry(this);
    endpoint.setCamelEndpoint(endpoint.getOutboundEndpoint().resolve(camelContext));
    String annotatedEndpointId = endpoint.getAnnotationEndpointId();
    if (annotatedEndpointId.isEmpty()) {
      endpoint.setEndpointId(
          String.format(
              ENDPOINT_ID_FORMAT,
              OUT_PREFIX,
              endpoint.getConsumedScenario().getID(),
              endpoint.getConnectorId()));
    } else {
      endpoint.setEndpointId(annotatedEndpointId);
    }
  }

  private void checkForDuplicateEndpointIds() {
    Set<String> set = new HashSet<>();
    List<String> inboundIds =
        inboundEndpointDefinitions.stream()
            .map(endpoint -> ((AnnotatedInboundEndpoint) endpoint).getEndpointId())
            .collect(Collectors.toList());
    inboundIds.forEach(id -> checkIfDuplicate(set, id));
    List<String> outboundIds =
        outboundEndpointDefinitions.stream()
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
