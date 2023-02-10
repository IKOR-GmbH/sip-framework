package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorGroupInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.IntegrationScenarioInfo;
import de.ikor.sip.foundation.core.declarative.DeclarationsRegistry;
import de.ikor.sip.foundation.core.declarative.connector.*;
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
   * @param connectorGroup from which info object is created
   * @return ConnectorGroupInfo
   */
  public static ConnectorGroupInfo createConnectorGroupInfo(
      DeclarationsRegistry declarationsRegistry, ConnectorGroupDefinition connectorGroup) {
    return ConnectorGroupInfo.builder()
        .connectorGroupId(connectorGroup.getID())
        .connectorGroupDescription(connectorGroup.getDocumentation())
        .inboundConnectors(
            fetchInboundConnectorIds(
                declarationsRegistry.getInboundConnectorsByConnectorGroupId(
                    connectorGroup.getID())))
        .outboundConnectors(
            fetchOutboundConnectorIds(
                declarationsRegistry.getOutboundEndpointsByConnectorGroupId(
                    connectorGroup.getID())))
        .build();
  }

  private static List<String> fetchInboundConnectorIds(
      List<InboundConnectorDefinition> inboundConnectors) {
    return inboundConnectors.stream()
        .map(ConnectorDefinition::getConnectorId)
        .collect(Collectors.toList());
  }

  private static List<String> fetchOutboundConnectorIds(
      List<OutboundConnectorDefinition> outboundConnectors) {
    return outboundConnectors.stream()
        .map(ConnectorDefinition::getConnectorId)
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
   * Creates initialized {@link ConnectorInfo} from {@link ConnectorDefinition}
   *
   * @param connector from which info object is created
   * @return ConnectorInfo
   */
  public static ConnectorInfo createAndAddEndpointInfo(ConnectorDefinition connector) {
    return ConnectorInfo.builder()
        .connectorId(connector.getConnectorId())
        .connectorType(connector.getConnectorType())
        .connectorGroupId(connector.getConnectorGroupId())
        .scenarioId(connector.getScenarioId())
        .build();
  }
}
