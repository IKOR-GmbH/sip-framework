package de.ikor.sip.foundation.core.declarative.definitions;

import de.ikor.sip.foundation.core.declarative.annotations.Connector;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class AConnectorDefinition implements ConnectorDefinition {

  @Override
  public String getID() {
    return getAnnotation().value();
  }

  @Override
  public String getDocumentation() {
    final var annotationPath = getAnnotation().pathToDocumentationResource();
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

  private Connector getAnnotation() {
    final var annotation =
        getClass().getAnnotation(Connector.class);
    if (null == annotation)
      throw new IllegalStateException(
          "@Connector annotation is missing on class " + getClass().getName());
    return annotation;
  }
}
