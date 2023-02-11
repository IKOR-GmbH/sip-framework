package de.ikor.sip.foundation.core.declarative.connectorgroup;

import de.ikor.sip.foundation.core.declarative.annonation.ConnectorGroup;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;

/** Connector specified by user through the annotation {@link ConnectorGroupBase} */
public class ConnectorGroupBase implements ConnectorGroupDefinition {

  private final ConnectorGroup annotation =
      DeclarativeHelper.getAnnotationOrThrow(ConnectorGroup.class, this);

  @Override
  public final String getID() {
    return annotation.groupId();
  }

  @Override
  public String getPathToDocumentationResource() {
    return annotation.pathToDocumentationResource();
  }
}
