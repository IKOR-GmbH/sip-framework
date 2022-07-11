package de.ikor.sip.foundation.core.declarative.definitions;

import de.ikor.sip.foundation.core.declarative.annotations.IntegrationScenario;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public abstract class AIntegrationScenarioDefinition<T>
    implements IntegrationScenarioDefinition<T> {

  @Override
  public String getID() {
    return getAnnotation().value();
  }

  @Override
  public String getDescription() {
    final var annotationPath = getAnnotation().pathToDocumentationResource();
    final var resourcePath =
        annotationPath.isEmpty()
            ? String.format("docs/integration-scenarios/%s.md", getID())
            : annotationPath;
    final var resource = new ClassPathResource(resourcePath);

    if (!resource.isReadable()) {
      return String.format(
          "No documentation has been provided for integration-scenario '%s'", getID());
    }

    try (var input = resource.getInputStream()) {
      return new String(input.readAllBytes());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read documentation resource", e);
    }
  }

  private IntegrationScenario getAnnotation() {
    final var annotation =
        getClass()
            .getAnnotation(
                IntegrationScenario.class);
    if (null == annotation)
      throw new IllegalStateException(
          "@IntegrationScenario annotation is missing on class " + getClass().getName());
    return annotation;
  }
}
