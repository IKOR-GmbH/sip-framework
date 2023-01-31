package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.InboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.OutboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@RestControllerEndpoint(id = "adapterdefinition")
public class DeclarativeDefinitionEndpoint {

  private static final String NO_RESPONSE = "NO RESPONSE";

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

    initializeConnectorInfoRegistry(inboundEndpoints, outboundEndpoints);
    initializeIntegrationScenarioInfoRegistry();
    initializeEndpointInfoRegistry(inboundEndpoints, outboundEndpoints);
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

  private void initializeConnectorInfoRegistry(
      List<InboundEndpointDefinition> inboundEndpoints,
      List<OutboundEndpointDefinition> outboundEndpoints) {
    for (ConnectorDefinition connector : declarationsRegistry.getConnectors()) {
      ConnectorInfo info = createConnectorInfo(connector, inboundEndpoints, outboundEndpoints);
      connectorInfoRegistry.add(info);
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

  private ConnectorInfo createConnectorInfo(
      ConnectorDefinition connectorDefinition,
      List<InboundEndpointDefinition> inboundEndpoints,
      List<OutboundEndpointDefinition> outboundEndpoints) {
    ConnectorInfo info = new ConnectorInfo();
    info.setConnectorId(connectorDefinition.getID());
    info.setConnectorDescription(connectorDefinition.getDocumentation());

    for (InboundEndpointDefinition endpoint : inboundEndpoints) {
      if (endpoint.getConnector().getID().equals(info.getConnectorId())) {
        info.getInboundEndpoints().add(endpoint.getEndpointId());
      }
    }

    for (OutboundEndpointDefinition endpoint : outboundEndpoints) {
      if (endpoint.getConnector().getID().equals(info.getConnectorId())) {
        info.getOutboundEndpoints().add(endpoint.getEndpointId());
      }
    }
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
