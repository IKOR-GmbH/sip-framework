package de.ikor.sip.foundation.core.declarative.scenario;

import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.orchestation.ConsumerOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
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
  public String getPathToDocumentationResource() {
    return scenarioAnnotation.pathToDocumentationResource();
  }
}
