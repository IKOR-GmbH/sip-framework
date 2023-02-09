package de.ikor.sip.foundation.core.declarative.connectorgroup;

import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;

/**
 * Connector specified by user through the annotation {@link ConnectorGroup}
 */
public class ConnectorGroup implements ConnectorGroupDefinition {

    private final de.ikor.sip.foundation.core.declarative.annonations.ConnectorGroup annotation =
            DeclarativeHelper.getAnnotationOrThrow(de.ikor.sip.foundation.core.declarative.annonations.ConnectorGroup.class, this);

    @Override
    public String getDocumentation() {
        return readDocumentation(annotation.pathToDocumentationResource());
    }

    @Override
    public final String getID() {
        return annotation.groupId();
    }
}
