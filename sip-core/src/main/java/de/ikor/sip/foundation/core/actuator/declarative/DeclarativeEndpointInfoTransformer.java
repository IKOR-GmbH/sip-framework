package de.ikor.sip.foundation.core.actuator.declarative;

import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorGroupInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.IntegrationScenarioInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.RouteInfo;
import de.ikor.sip.foundation.core.declarative.connector.*;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.camel.Endpoint;
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
      List<ConnectorInfo> connectors, ConnectorGroupDefinition connectorGroup) {

    return ConnectorGroupInfo.builder()
        .connectorGroupId(connectorGroup.getId())
        .connectors(connectors)
        .connectorGroupDescription(
            readDocumentation(
                CONNECTOR_GROUP_DEFAULT_DOCS_PATH,
                connectorGroup.getPathToDocumentationResource(),
                connectorGroup.getId()))
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
        .scenarioId(scenario.getId())
        .requestModelClass(scenario.getRequestModelClass().getName())
        .responseModelClass(
            scenario.getResponseModelClass().map(Class::getName).orElse(NO_RESPONSE))
        .scenarioDescription(
            readDocumentation(
                INTEGRATION_SCENARIO_DEFAULT_DOCS_PATH,
                scenario.getPathToDocumentationResource(),
                scenario.getId()))
        .build();
  }

  /**
   * Creates initialized {@link ConnectorInfo} from {@link ConnectorDefinition}
   *
   * @param connector from which info object is created
   * @return ConnectorInfo
   */
  public static ConnectorInfo createAndAddConnectorInfo(
      ConnectorDefinition connector, List<RouteInfo> routes, List<Endpoint> endpoints) {
    return ConnectorInfo.builder()
        .connectorId(connector.getId())
        .connectorType(connector.getConnectorType())
        .connectorGroupId(connector.getConnectorGroupId())
        .camelEndpointUris(
            endpoints.stream().map(Endpoint::getEndpointKey).collect(Collectors.toList()))
        .scenarioId(connector.getScenarioId())
        .routes(routes)
        .connectorDescription(
            readDocumentation(
                CONNECTORS_DEFAULT_DOCS_PATH,
                connector.getPathToDocumentationResource(),
                connector.getId()))
        .requestModelClass(connector.getRequestModelClass().getName())
        .responseModelClass(
            connector.getResponseModelClass().map(Class::getName).orElse(NO_RESPONSE))
        .build();
  }

  private static String readDocumentation(String defaultDocsPath, String path, String id) {
    final var resourcePath = path.isEmpty() ? String.format("%s/%s.md", defaultDocsPath, id) : path;
    final var resource = new ClassPathResource(resourcePath);

    if (!resource.isReadable()) {
      return null;
    }

    try (var input = resource.getInputStream()) {
      return new String(input.readAllBytes());
    } catch (IOException e) {
      throw new SIPFrameworkException("Failed to read documentation resource", e);
    }
  }
}
