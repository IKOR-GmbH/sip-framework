package de.ikor.sip.foundation.core.declarative.composite;

import de.ikor.sip.foundation.core.declarative.annonation.CompositeProcess;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;

public class CompositeProcessBase implements CompositeProcessDefinition {

  public Orchestrator<CompositeOrchestrationInfo> getOrchestrator() {
    // TODO: Throw better error
    throw new NotImplementedException();
  }

  private final CompositeProcess processAnnotation =
      DeclarativeHelper.getAnnotationOrThrow(CompositeProcess.class, this);

  public final String getId() {
    return processAnnotation.processId();
  }

  public String getPathToDocumentationResource() {
    return processAnnotation.pathToDocumentationResource();
  }

  public List<Class<? extends IntegrationScenarioDefinition>> getConsumerDefinitions() {
    return List.of(processAnnotation.consumers());
  }

  public List<Class<? extends IntegrationScenarioDefinition>> getProviderDefinitions() {
    return List.of(processAnnotation.providers());
  }
}
