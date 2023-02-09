package de.ikor.sip.foundation.core.actuator.declarative;

import static de.ikor.sip.foundation.core.actuator.declarative.DeclarativeModelTransformer.*;

import de.ikor.sip.foundation.core.actuator.declarative.model.*;
import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.actuate.endpoint.web.Link;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/** Actuator endpoints for exposing Connectors, Integration Scenarios and Endpoints. */
@Component
@RestControllerEndpoint(id = "adapterdefinition")
public class DeclarativeDefinitionEndpoint {

  private static final String CONNECTORS_PATH = "connectors";
  private static final String SCENARIOS_PATH = "scenarios";
  private static final String ENDPOINTS_PATH = "endpoints";
  private static final String URI_FORMAT = "%s/%s";

  private final DeclarationsRegistry declarationsRegistry;
  private final List<ConnectorInfo> connectors = new ArrayList<>();
  private final List<IntegrationScenarioInfo> scenarios = new ArrayList<>();
  private final List<EndpointInfo> endpoints = new ArrayList<>();

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
  public List<ConnectorInfo> getConnectorInfo() {
    return connectors;
  }

  @GetMapping("/scenarios")
  public List<IntegrationScenarioInfo> getScenarioInfo() {
    return scenarios;
  }

  @GetMapping("/endpoints")
  public List<EndpointInfo> getEndpointInfo() {
    return endpoints;
  }

  private void initializeConnectorInfos() {
    for (ConnectorDefinition connector : declarationsRegistry.getConnectors()) {
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
        .getEndpoints()
        .forEach(endpoint -> endpoints.add(createAndAddEndpointInfo(endpoint)));
  }
}
