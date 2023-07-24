package de.ikor.sip.foundation.core.declarative.composite;

import de.ikor.sip.foundation.core.declarative.annonation.CompositeProcess;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;

public class CompositeProcessBase implements CompositeProcessDefinition {

  public Orchestrator<CompositeOrchestrationInfo> getOrchestrator() {
    throw new NotImplementedException();
  }

  private final CompositeProcess scenarioAnnotation =
      DeclarativeHelper.getAnnotationOrThrow(CompositeProcess.class, this);

  public final String getId() {
    return scenarioAnnotation.compositeScenarioId();
  }

  public String getPathToDocumentationResource() {
    return scenarioAnnotation.pathToDocumentationResource();
  }

  public List<Class<? extends IntegrationScenarioDefinition>> getConsumerDefinitions() {
    return List.of(scenarioAnnotation.consumers());
  }

  public List<Class<? extends IntegrationScenarioDefinition>> getProviderDefinitions() {
    return List.of(scenarioAnnotation.providers());
  }
}
