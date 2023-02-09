package de.ikor.sip.foundation.core.declarative.scenario;

import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.orchestation.ConsumerOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Optional;

public abstract class IntegrationScenarioBase implements IntegrationScenarioDefinition {

    private final IntegrationScenario scenarioAnnotation =
            DeclarativeHelper.getAnnotationOrThrow(IntegrationScenario.class, this);

    @Override
    public Orchestrator<ConsumerOrchestrationInfo> getOrchestrator() {
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
                        ? String.format("documents/structure/integration-scenarios/%s", getID())
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
