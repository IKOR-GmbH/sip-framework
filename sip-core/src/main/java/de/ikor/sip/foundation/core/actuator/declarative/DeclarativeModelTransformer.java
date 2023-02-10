package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorGroupInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.IntegrationScenarioInfo;
import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.connector.GenericInboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connector.GenericOutboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
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
   * Creates initialized {@link ConnectorGroupInfo} from {@link ConnectorGroupDefinition}
   *
   * @param declarationsRegistry for fetching the endpoints related to connector
   * @param connector from which info object is created
   * @return ConnectorInfo
   */
  public static ConnectorGroupInfo createConnectorInfo(
      DeclarationsRegistry declarationsRegistry, ConnectorGroupDefinition connector) {
    return ConnectorGroupInfo.builder()
        .connectorGroupId(connector.getID())
        .connectorGroupDescription(connector.getDocumentation())
        .inboundConnectors(fetchInboundEndpointIds(declarationsRegistry, connector.getID()))
        .outboundConnectors(fetchOutboundEndpointIds(declarationsRegistry, connector.getID()))
        .build();
  }

  private static List<String> fetchInboundEndpointIds(
      DeclarationsRegistry declarationsRegistry, String connectorId) {
    return declarationsRegistry.getInboundEndpointsByConnectorId(connectorId).stream()
        .map(endpoint -> endpoint.getConnectorId())
        .collect(Collectors.toList());
  }

  private static List<String> fetchOutboundEndpointIds(
      DeclarationsRegistry declarationsRegistry, String connectorId) {
    return declarationsRegistry.getOutboundEndpointsByConnectorId(connectorId).stream()
        .map(endpoint -> endpoint.getConnectorId())
        .collect(Collectors.toList());
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
   * Creates initialized {@link ConnectorInfo} from {@link GenericInboundConnectorBase}
   *
   * @param endpoint from which info object is created
   * @return EndpointInfo
   */
  public static ConnectorInfo createAndAddInboundEndpoint(InboundConnectorDefinition endpoint) {
    return ConnectorInfo.builder()
        .connectorId(endpoint.getConnectorId())
        .connectorType(endpoint.getConnectorType())
        .connectorGroupId(endpoint.getConnectorGroupId())
        .scenarioId(endpoint.getScenarioId())
        .build();
  }

  /**
   * Creates initialized {@link ConnectorInfo} from {@link GenericOutboundConnectorBase}
   *
   * @param endpoint from which info object is created
   * @return EndpointInfo
   */
  public static ConnectorInfo createAndAddOutboundEndpoint(OutboundConnectorDefinition endpoint) {
    return ConnectorInfo.builder()
        .connectorId(endpoint.getConnectorId())
        .connectorType(endpoint.getConnectorType())
        .connectorGroupId(endpoint.getConnectorGroupId())
        .scenarioId(endpoint.getScenarioId())
        .build();
  }
}
