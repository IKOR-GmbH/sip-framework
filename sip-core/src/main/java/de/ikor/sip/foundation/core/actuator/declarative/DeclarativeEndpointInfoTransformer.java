package de.ikor.sip.foundation.core.actuator.declarative;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorGroupInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.ConnectorInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.IntegrationScenarioInfo;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

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
  private static final String MARKDOWN_EXTENSION = ".md";

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

  private static String readDocumentation(String defaultDocsPath, String path, String id) {
    if (path.isEmpty()) {
      return findFileByIdAndGetContent(defaultDocsPath, id);
    }
    return getSpecifiedFileContent(path);
  }

  private static String findFileByIdAndGetContent(String defaultDocsPath, String id) {
    try {
      List<Path> resources = getResourcesFromClasspath(defaultDocsPath);

      Optional<Path> file =
          resources.stream()
              .filter(
                  resource -> resource
                      .getFileName()
                      .toString()
                      .equalsIgnoreCase(id + MARKDOWN_EXTENSION))
              .findFirst();

      if (file.isPresent()) {
        Path filePath = file.get();
        return Files.readString(filePath);
      }
      return null;
    } catch (IOException | URISyntaxException e) {
      throw SIPFrameworkException.init("Failed to read documentation resource", e);
    }
  }

  private static String getSpecifiedFileContent(String resourcePath) {
    final var resource = new ClassPathResource(resourcePath);

    if (!resource.isReadable()) {
      return null;
    }

    try (var input = resource.getInputStream()) {
      return new String(input.readAllBytes());
    } catch (IOException e) {
      throw SIPFrameworkException.init("Failed to read documentation resource", e);
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

  private static List<Path> getResourcesFromClasspath(String defaultPath)
      throws IOException, URISyntaxException {
    List<Path> resources = new ArrayList<>();

    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    Enumeration<URL> urls = classLoader.getResources(defaultPath);
    while (urls.hasMoreElements()) {
      URL url = urls.nextElement();
      if (url.getProtocol().equals("file")) {
        Path path = Paths.get(url.toURI());
        resources.addAll(getAllFiles(path));
      }
    }

    return resources;
  }

  private static List<Path> getAllFiles(Path directory) throws IOException {
    List<Path> files = new ArrayList<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
      for (Path path : stream) {
        if (Files.isDirectory(path)) {
          files.addAll(getAllFiles(path));
        } else {
          files.add(path);
        }
      }
    }
    return files;
  }
}
