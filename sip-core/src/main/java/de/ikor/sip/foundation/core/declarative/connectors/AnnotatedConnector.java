package de.ikor.sip.foundation.core.declarative.connectors;

import de.ikor.sip.foundation.core.declarative.annonations.Connector;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;

/** Connector specified by user through the annotation {@link AnnotatedConnector} */
public class AnnotatedConnector implements ConnectorDefinition {

  private final Connector annotation =
      DeclarativeHelper.getAnnotationOrThrow(Connector.class, this);

  @Override
  public final String getID() {
    return annotation.connectorId();
  }

  @Override
  public String getDocumentation() {
    return readDocumentation(annotation.pathToDocumentationResource());
  }
}
