package de.ikor.sip.foundation.core.declarative.connectors;

import de.ikor.sip.foundation.core.declarative.annonations.Connector;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;
import java.io.IOException;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;

public class AnnotatedConnector implements ConnectorDefinition {

  private final Connector annotation = ReflectionHelper.getAnnotationOrThrow(Connector.class, this);

  @Override
  public final String getID() {
    return annotation.connectorId();
  }

  @Override
  public String getDocumentation() {
    final var annotationPath = annotation.pathToDocumentationResource();
    final var resourcePath =
        annotationPath.isEmpty() ? String.format("docs/connectors/%s.md", getID()) : annotationPath;
    final var resource = new ClassPathResource(resourcePath);

    if (!resource.isReadable()) {
      return String.format("No documentation has been provided for connector '%s'", getID());
    }

    try (var input = resource.getInputStream()) {
      return new String(input.readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read documentation resource", e);
    }
  }

  @Override
  public final Map<String, IntegrationScenarioConsumerDefinition>
      getConsumedIntegrationScenarios() {
    throw new UnsupportedOperationException();
  }

  @Override
  public final Map<String, IntegrationScenarioProviderDefinition>
      getProvidedIntegrationScenarios() {
    throw new UnsupportedOperationException();
  }
}