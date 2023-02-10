package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorGroupInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.IntegrationScenarioInfo;
import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.ikor.sip.foundation.core.actuator.declarative.DeclarativeModelTransformer.createAndAddInboundEndpoint;
import static de.ikor.sip.foundation.core.actuator.declarative.DeclarativeModelTransformer.createAndAddOutboundEndpoint;
import static de.ikor.sip.foundation.core.actuator.declarative.DeclarativeModelTransformer.createConnectorInfo;
import static de.ikor.sip.foundation.core.actuator.declarative.DeclarativeModelTransformer.createIntegrationScenarioInfo;

/** Actuator endpoints for exposing Connectors, Integration Scenarios and Endpoints. */
@Component
@RestControllerEndpoint(id = "adapterdefinition")
public class DeclarativeDefinitionEndpoint {

  private static final String CONNECTORS_PATH = "connectors";
  private static final String SCENARIOS_PATH = "scenarios";
  private static final String ENDPOINTS_PATH = "endpoints";
  private static final String URI_FORMAT = "%s/%s";

  private final DeclarationsRegistry declarationsRegistry;
  private final List<ConnectorGroupInfo> connectors = new ArrayList<>();
  private final List<IntegrationScenarioInfo> scenarios = new ArrayList<>();
  private final List<ConnectorInfo> endpoints = new ArrayList<>();

  public DeclarativeDefinitionEndpoint(DeclarationsRegistry declarationsRegistry) {
    this.declarationsRegistry = declarationsRegistry;
  }

  @PostConstruct
  private void collectInfo() {
    initializeConnectorInfos();
    initializeIntegrationScenarioInfos();
    initializeEndpointInfos();
  }

  /**
   * Base endpoint which exposes other child endpoints for connectors, scenarios and endpoints.
   *
   * @param request HttpServletRequest
   * @return links Map<String, Link>
   */
  @GetMapping
  public Map<String, Link> getLinks(HttpServletRequest request) {
    String basePath = request.getRequestURL().toString();

    Link connectorsUri = new Link(String.format(URI_FORMAT, basePath, CONNECTORS_PATH));
    Link scenariosUri = new Link(String.format(URI_FORMAT, basePath, SCENARIOS_PATH));
    Link endpointsUri = new Link(String.format(URI_FORMAT, basePath, ENDPOINTS_PATH));

    return Map.of(
        CONNECTORS_PATH, connectorsUri, SCENARIOS_PATH, scenariosUri, ENDPOINTS_PATH, endpointsUri);
  }

  @GetMapping("/connectors")
  public List<ConnectorGroupInfo> getConnectorInfo() {
    return connectors;
  }

  @GetMapping("/scenarios")
  public List<IntegrationScenarioInfo> getScenarioInfo() {
    return scenarios;
  }

  @GetMapping("/endpoints")
  public List<ConnectorInfo> getEndpointInfo() {
    return endpoints;
  }

  private void initializeConnectorInfos() {
    for (ConnectorGroupDefinition connector : declarationsRegistry.getConnectors()) {
      connectors.add(createConnectorInfo(declarationsRegistry, connector));
    }
  }

  private void initializeIntegrationScenarioInfos() {
    for (IntegrationScenarioDefinition scenario : declarationsRegistry.getScenarios()) {
      scenarios.add(createIntegrationScenarioInfo(scenario));
    }
  }

  private void initializeEndpointInfos() {
    declarationsRegistry
        .getInboundEndpoints()
        .forEach(
            endpoint ->
                endpoints.add(createAndAddInboundEndpoint(endpoint)));
    declarationsRegistry
        .getOutboundEndpoints()
        .forEach(
            endpoint ->
                endpoints.add(createAndAddOutboundEndpoint(endpoint)));
  }
}
