package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.InboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.OutboundEndpointDefinition;
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

@Component
@RestControllerEndpoint(id = "adapterdefinition")
public class DeclarativeDefinitionEndpoint {

  private static final String NO_RESPONSE = "NO RESPONSE";
  private static final String CONNECTORS = "connectors";
  private static final String SCENARIOS = "scenarios";
  private static final String ENDPOINTS = "endpoints";
  private static final String URI_FORMAT = "%s/%s";

  private final DeclarationsRegistry declarationsRegistry;
  private final List<ConnectorInfo> connectorInfoRegistry = new ArrayList<>();
  private final List<IntegrationScenarioInfo> integrationScenarioInfoRegistry = new ArrayList<>();
  private final List<EndpointInfo> endpointInfoRegistry = new ArrayList<>();

  public DeclarativeDefinitionEndpoint(DeclarationsRegistry declarationsRegistry) {
    this.declarationsRegistry = declarationsRegistry;
  }

  @PostConstruct
  private void collectInfo() {
    List<InboundEndpointDefinition> inboundEndpoints = declarationsRegistry.getInboundEndpoints();
    List<OutboundEndpointDefinition> outboundEndpoints =
        declarationsRegistry.getOutboundEndpoints();

    initializeConnectorInfoRegistry();
    initializeIntegrationScenarioInfoRegistry();
    initializeEndpointInfoRegistry(inboundEndpoints, outboundEndpoints);
  }

  @GetMapping
  public Map<String, Link> getLinks(HttpServletRequest request) {
    String basePath = request.getRequestURL().toString();

    Link connectorsUri = new Link(String.format(URI_FORMAT, basePath, CONNECTORS));
    Link scenariosUri = new Link(String.format(URI_FORMAT, basePath, SCENARIOS));
    Link endpointsUri = new Link(String.format(URI_FORMAT, basePath, ENDPOINTS));

    return Map.of(CONNECTORS, connectorsUri, SCENARIOS, scenariosUri, ENDPOINTS, endpointsUri);
  }

  @GetMapping("/connectors")
  public List<ConnectorInfo> getConnectorInfo() {
    return connectorInfoRegistry;
  }

  @GetMapping("/scenarios")
  public List<IntegrationScenarioInfo> getScenarioInfo() {
    return integrationScenarioInfoRegistry;
  }

  @GetMapping("/endpoints")
  public List<EndpointInfo> getEndpointInfo() {
    return endpointInfoRegistry;
  }

  private void initializeConnectorInfoRegistry() {
    for (ConnectorDefinition connector : declarationsRegistry.getConnectors()) {
      connectorInfoRegistry.add(createConnectorInfo(connector));
    }
  }

  private void initializeIntegrationScenarioInfoRegistry() {
    for (IntegrationScenarioDefinition scenario : declarationsRegistry.getIntegrationScenarios()) {
      IntegrationScenarioInfo info = createIntegrationScenarioInfo(scenario);
      integrationScenarioInfoRegistry.add(info);
    }
  }

  private void initializeEndpointInfoRegistry(
      List<InboundEndpointDefinition> inboundEndpoints,
      List<OutboundEndpointDefinition> outboundEndpoints) {
    inboundEndpoints.forEach(
        endpoint -> createAndAdd(endpoint.getEndpointId(), endpoint.getInboundEndpoint().getUri()));
    outboundEndpoints.forEach(
        endpoint ->
            createAndAdd(endpoint.getEndpointId(), endpoint.getOutboundEndpoint().getUri()));
  }

  private ConnectorInfo createConnectorInfo(ConnectorDefinition connector) {
    ConnectorInfo info = new ConnectorInfo();
    info.setConnectorId(connector.getID());
    info.setConnectorDescription(connector.getDocumentation());

    declarationsRegistry
        .getInboundEndpointsByConnectorId(connector.getID())
        .forEach(endpoint -> info.getInboundEndpoints().add(endpoint.getEndpointId()));

    declarationsRegistry
        .getOutboundEndpointsByConnectorId(connector.getID())
        .forEach(endpoint -> info.getOutboundEndpoints().add(endpoint.getEndpointId()));

    return info;
  }

  private IntegrationScenarioInfo createIntegrationScenarioInfo(
      IntegrationScenarioDefinition scenario) {
    IntegrationScenarioInfo info = new IntegrationScenarioInfo();
    info.setScenarioId(scenario.getID());
    info.setScenarioDescription(scenario.getDescription());
    info.setRequestModelClass(scenario.getRequestModelClass().getName());
    info.setResponseModelClass(
        scenario.getResponseModelClass().isPresent()
            ? scenario.getResponseModelClass().get().getName()
            : NO_RESPONSE);
    return info;
  }

  private void createAndAdd(String endpointId, String uri) {
    EndpointInfo info = new EndpointInfo(endpointId, uri);
    endpointInfoRegistry.add(info);
  }
}
