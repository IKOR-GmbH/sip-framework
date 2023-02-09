package de.ikor.sip.foundation.core.declarative.connectorgroup;

import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;

/**
 * Connector specified by user through the annotation {@link ConnectorGroupBase}
 */
public class ConnectorGroupBase implements ConnectorGroupDefinition {

    private final de.ikor.sip.foundation.core.declarative.annonation.ConnectorGroup annotation =
            DeclarativeHelper.getAnnotationOrThrow(de.ikor.sip.foundation.core.declarative.annonation.ConnectorGroup.class, this);

    @Override
    public String getDocumentation() {
        return readDocumentation(annotation.pathToDocumentationResource());
    }

    @Override
    public final String getID() {
        return annotation.groupId();
    }
}
