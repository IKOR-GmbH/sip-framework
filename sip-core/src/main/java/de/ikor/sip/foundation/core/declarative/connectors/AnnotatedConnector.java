package de.ikor.sip.foundation.core.declarative.connectors;

import de.ikor.sip.foundation.core.declarative.annonations.Connector;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;

public class AnnotatedConnector implements ConnectorDefinition {

  private final Connector annotation =
      DeclarativeHelper.getAnnotationOrThrow(Connector.class, this);

  @Override
  public final String getID() {
    return annotation.connectorId();
  }

  @Override
  public String getDocumentation() {
    final var annotationPath = annotation.pathToDocumentationResource();
    final var resourcePath =
        annotationPath.isEmpty()
            ? String.format("documents/structure/connectors/%s", getID())
            : annotationPath;
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
}
