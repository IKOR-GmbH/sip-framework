package de.ikor.sip.foundation.core.declarative.process;

import de.ikor.sip.foundation.core.declarative.annonation.CompositeProcess;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.List;

public class CompositeProcessBase implements CompositeProcessDefinition {

  public Orchestrator<CompositeOrchestrationInfo> getOrchestrator() {
    throw SIPFrameworkInitializationException.init(
        "Orchestration needs to be defined for the process '%s' declared in the class '%s'. Please @Override getOrchestrator() method.",
        getId(), getClass().getName());
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
