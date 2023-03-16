package de.ikor.sip.foundation.core.declarative.scenario;

import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.ScenarioOrchestrator;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import java.util.Optional;

/**
 * Base class for an integration scenario definition.
 *
 * <p>Adapter developers should extend this class and annotate it with @{@link IntegrationScenario}.
 *
 * @see IntegrationScenario
 */
public abstract class IntegrationScenarioBase implements IntegrationScenarioDefinition {

  private final IntegrationScenario scenarioAnnotation =
      DeclarativeHelper.getAnnotationOrThrow(IntegrationScenario.class, this);

  @Override
  public Orchestrator<ScenarioOrchestrationInfo> getOrchestrator() {
    return ScenarioOrchestrator.forScenario(this);
  }

  @Override
  public final String getId() {
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
  public String getPathToDocumentationResource() {
    return scenarioAnnotation.pathToDocumentationResource();
  }
}
