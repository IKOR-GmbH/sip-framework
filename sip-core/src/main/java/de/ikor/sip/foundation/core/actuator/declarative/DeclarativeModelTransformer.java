package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.EndpointInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.IntegrationScenarioInfo;
import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.connectors.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.*;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Util transformer class with methods for transforming framework declarative objects to their
 * corresponding POJO forms used for exposing its values.
 */
public class DeclarativeModelTransformer {

  private static final String NO_RESPONSE = "NO RESPONSE";

  private DeclarativeModelTransformer() {}

  /**
   * Creates initialized {@link ConnectorInfo} from {@link ConnectorDefinition}
   *
   * @param declarationsRegistry for fetching the endpoints related to connector
   * @param connector from which info object is created
   * @return ConnectorInfo
   */
  public static ConnectorInfo createConnectorInfo(
      DeclarationsRegistry declarationsRegistry, ConnectorDefinition connector) {
    return ConnectorInfo.builder()
        .connectorId(connector.getID())
        .connectorDescription(connector.getDocumentation())
        .inboundEndpoints(
            fetchInboundEndpointIds(
                declarationsRegistry.getInboundEndpointsByConnectorId(connector.getID())))
        .outboundEndpoints(
            fetchOutboundEndpointIds(
                declarationsRegistry.getOutboundEndpointsByConnectorId(connector.getID())))
        .build();
  }

  /**
   * Creates initialized {@link IntegrationScenarioInfo} from {@link IntegrationScenarioDefinition}
   *
   * @param scenario from which info object is created
   * @return IntegrationScenarioInfo
   */
  public static IntegrationScenarioInfo createIntegrationScenarioInfo(
      IntegrationScenarioDefinition scenario) {
    return IntegrationScenarioInfo.builder()
        .scenarioId(scenario.getID())
        .scenarioDescription(scenario.getDescription())
        .requestModelClass(scenario.getRequestModelClass().getName())
        .responseModelClass(
            scenario.getResponseModelClass().isPresent()
                ? scenario.getResponseModelClass().get().getName()
                : NO_RESPONSE)
        .build();
  }

  /**
   * Creates initialized {@link EndpointInfo} from {@link EndpointDefinition}
   *
   * @param endpoint from which info object is created
   * @return EndpointInfo
   */
  public static EndpointInfo createAndAddEndpointInfo(EndpointDefinition endpoint) {
    return EndpointInfo.builder()
        .endpointId(endpoint.getEndpointId())
        .endpointType(endpoint.getEndpointType())
        .connectorId(endpoint.getConnectorId())
        .scenarioId(endpoint.getScenarioId())
        .build();
  }

  private static List<String> fetchInboundEndpointIds(
      List<InboundEndpointDefinition> inboundEndpoints) {
    return inboundEndpoints.stream()
        .map(EndpointDefinition::getEndpointId)
        .collect(Collectors.toList());
  }

  private static List<String> fetchOutboundEndpointIds(
      List<OutboundEndpointDefinition> outboundEndpoints) {
    return outboundEndpoints.stream()
        .map(EndpointDefinition::getEndpointId)
        .collect(Collectors.toList());
  }
}
