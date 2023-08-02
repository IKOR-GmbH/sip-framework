package de.ikor.sip.foundation.core.actuator.declarative;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import de.ikor.sip.foundation.core.actuator.declarative.model.*;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.connector.*;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeOrchestrator;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

/**
 * Util transformer class with methods for transforming framework declarative objects to their
 * corresponding POJO forms used for exposing its values.
 */
@Slf4j
public class DeclarativeEndpointInfoTransformer {

  private static final String CONNECTOR_GROUP_DEFAULT_DOCS_PATH =
      "documents/structure/connector-groups";
  private static final String INTEGRATION_SCENARIO_DEFAULT_DOCS_PATH =
      "documents/structure/integration-scenarios";
  private static final String CONNECTORS_DEFAULT_DOCS_PATH = "documents/structure/connectors";
  private static final String PROCESSES_DEFAULT_DOCS_PATH = "documents/structure/processes";

  private DeclarativeEndpointInfoTransformer() {}

  /**
   * Creates initialized {@link ConnectorGroupInfo} from {@link ConnectorGroupDefinition} and a list
   * of it's {@link ConnectorInfo connectors}
   *
   * @param connectors that are a part of this connector group
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
      IntegrationScenarioDefinition scenario, JsonSchemaGenerator schemaGen) {
    Class<?> responseModelClass = scenario.getResponseModelClass().orElse(null);
    return IntegrationScenarioInfo.builder()
        .scenarioId(scenario.getId())
        .requestModelClass(scenario.getRequestModelClass().getName())
        .requestJsonForm(createJsonSchema(schemaGen, scenario.getRequestModelClass()))
        .responseModelClass(scenario.getResponseModelClass().map(Class::getName).orElse(null))
        .responseJsonForm(createJsonSchema(schemaGen, responseModelClass))
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
      ConnectorDefinition connector, RoutesRegistry routesRegistry, JsonSchemaGenerator schemaGen) {
    Class<?> responseModelClass = connector.getResponseModelClass().orElse(null);
    return ConnectorInfo.builder()
        .connectorId(connector.getId())
        .connectorType(connector.getConnectorType())
        .connectorGroupId(connector.getConnectorGroupId())
        .endpoints(routesRegistry.getExternalEndpointInfosForConnector(connector))
        .scenarioId(connector.getScenarioId())
        .routes(routesRegistry.getRoutesInfo(connector))
        .connectorDescription(
            readDocumentation(
                CONNECTORS_DEFAULT_DOCS_PATH,
                connector.getPathToDocumentationResource(),
                connector.getId()))
        .requestModelClass(connector.getRequestModelClass().getName())
        .requestJsonForm(createJsonSchema(schemaGen, connector.getRequestModelClass()))
        .responseModelClass(connector.getResponseModelClass().map(Class::getName).orElse(null))
        .responseJsonForm(createJsonSchema(schemaGen, responseModelClass))
        .build();
  }

  /**
   * Creates initialized {@link CompositeProcessInfo} from {@link CompositeProcessDefinition}
   *
   * @param compositeProcessDefinition from which info object is created
   * @return CompositeProcessInfo
   */
  public static CompositeProcessInfo createCompositeProcessInfo(
      CompositeProcessDefinition compositeProcessDefinition,
      IntegrationScenarioDefinition provider,
      List<IntegrationScenarioDefinition> consumers) {

    return CompositeProcessInfo.builder()
        .processId(compositeProcessDefinition.getId())
        .providerId(provider.getId())
        .consumerIds(consumers.stream().map(IntegrationScenarioDefinition::getId).toList())
        .orchestrationDefinition(
            compositeProcessDefinition.getOrchestrator() instanceof CompositeOrchestrator
                ? ((CompositeOrchestrator) compositeProcessDefinition.getOrchestrator())
                    .getOrchestrationDefinition()
                    .orElse(null)
                : null)
        .processDescription(
            readDocumentation(
                PROCESSES_DEFAULT_DOCS_PATH,
                compositeProcessDefinition.getPathToDocumentationResource(),
                compositeProcessDefinition.getId()))
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

  private static JsonSchema createJsonSchema(JsonSchemaGenerator schemaGen, Class<?> classModel) {
    if (classModel == null) {
      return null;
    }
    try {
      return schemaGen.generateSchema(classModel);
    } catch (JsonMappingException e) {
      log.debug("sip.core.runtimetest.json.schema_{}", classModel);
    }
    return null;
  }
}
