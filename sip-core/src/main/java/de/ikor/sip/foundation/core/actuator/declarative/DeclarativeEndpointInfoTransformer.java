package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorGroupInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.IntegrationScenarioInfo;
import de.ikor.sip.foundation.core.declarative.connector.*;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;

/**
 * Util transformer class with methods for transforming framework declarative objects to their
 * corresponding POJO forms used for exposing its values.
 */
public class DeclarativeEndpointInfoTransformer {

  private static final String NO_RESPONSE = "NO RESPONSE";

  private static final String CONNECTOR_GROUP_DEFAULT_DOCS_PATH =
      "documents/structure/connector-groups";
  private static final String INTEGRATION_SCENARIO_DEFAULT_DOCS_PATH =
      "documents/structure/integration-scenarios";
  private static final String CONNECTORS_DEFAULT_DOCS_PATH = "documents/structure/connectors";

  private DeclarativeEndpointInfoTransformer() {}

  /**
   * TODO: update javadocs Creates initialized {@link ConnectorGroupInfo} from {@link
   * ConnectorGroupDefinition}
   *
   * @param connectorGroup from which info object is created
   * @return ConnectorGroupInfo
   */
  public static ConnectorGroupInfo createConnectorGroupInfo(
      List<? extends ConnectorDefinition> inboundConnectors,
      List<? extends ConnectorDefinition> outboundConnectors,
      ConnectorGroupDefinition connectorGroup) {
    return ConnectorGroupInfo.builder()
        .connectorGroupId(connectorGroup.getID())
        .inboundConnectors(mapConnectorsToIds(inboundConnectors))
        .outboundConnectors(mapConnectorsToIds(outboundConnectors))
        .connectorGroupDescription(
            readDocumentation(
                CONNECTOR_GROUP_DEFAULT_DOCS_PATH,
                connectorGroup.getPathToDocumentationResource(),
                connectorGroup.getID()))
        .build();
  }

  private static List<String> mapConnectorsToIds(
      List<? extends ConnectorDefinition> connectorDefinitions) {
    return connectorDefinitions.stream()
        .map(ConnectorDefinition::getId)
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
        .requestModelClass(scenario.getRequestModelClass().getName())
        .responseModelClass(
            scenario.getResponseModelClass().map(Class::getName).orElse(NO_RESPONSE))
        .scenarioDescription(
            readDocumentation(
                INTEGRATION_SCENARIO_DEFAULT_DOCS_PATH,
                scenario.getPathToDocumentationResource(),
                scenario.getID()))
        .build();
  }

  /**
   * Creates initialized {@link ConnectorInfo} from {@link ConnectorDefinition}
   *
   * @param connector from which info object is created
   * @return ConnectorInfo
   */
  public static ConnectorInfo createAndAddConnectorInfo(ConnectorDefinition connector) {
    return ConnectorInfo.builder()
        .connectorId(connector.getId())
        .connectorType(connector.getConnectorType())
        .connectorGroupId(connector.getConnectorGroupId())
        .scenarioId(connector.getScenarioId())
        .connectorDescription(
            readDocumentation(
                CONNECTORS_DEFAULT_DOCS_PATH,
                connector.getPathToDocumentationResource(),
                connector.getId()))
        .build();
  }

  private static String readDocumentation(String defaultDocsPath, String path, String id) {
    final var resourcePath = path.isEmpty() ? String.format("%s/%s", defaultDocsPath, id) : path;
    final var resource = new ClassPathResource(resourcePath);

    if (!resource.isReadable()) {
      return String.format("No documentation has been provided for element with id: '%s'", id);
    }

    try (var input = resource.getInputStream()) {
      return new String(input.readAllBytes());
    } catch (IOException e) {
      throw new SIPFrameworkException("Failed to read documentation resource", e);
    }
  }
}
