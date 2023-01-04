package de.ikor.sip.foundation.core.declarative.scenario;

import de.ikor.sip.foundation.core.declarative.annonations.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.utils.ReflectionHelper;
import java.io.IOException;
import java.util.Optional;
import org.springframework.core.io.ClassPathResource;

public abstract class AnnotatedScenario implements IntegrationScenarioDefinition {

  private final IntegrationScenario scenarioAnnotation =
      ReflectionHelper.getAnnotationOrThrow(IntegrationScenario.class, this);

  @Override
  public Orchestrator<ConnectorOrchestrationInfo> getOrchestrator() {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public final String getID() {
    return scenarioAnnotation.scenarioId();
  }

  @Override
  public final Class<? extends Object> getRequestModelClass() {
    return scenarioAnnotation.requestModel();
  }

  @Override
  public final Optional<Class<? extends Object>> getResponseModelClass() {
    var responseModel = scenarioAnnotation.responseModel();
    return responseModel.equals(Void.class) ? Optional.empty() : Optional.of(responseModel);
  }

  @Override
  public String getDescription() {
    final var annotationPath = scenarioAnnotation.pathToDocumentationResource();
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
}
