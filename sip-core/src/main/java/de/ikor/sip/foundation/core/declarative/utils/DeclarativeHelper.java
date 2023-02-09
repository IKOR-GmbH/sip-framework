package de.ikor.sip.foundation.core.declarative.utils;

import de.ikor.sip.foundation.core.declarative.connectors.ConnectorType;

import java.lang.annotation.Annotation;

public class DeclarativeHelper {
    public static final String ENDPOINT_ID_FORMAT = "%s-%s-%s";

    private DeclarativeHelper() {
    }

    public static <A extends Annotation> A getAnnotationOrThrow(Class<A> annotation, Object from) {
        var ann = from.getClass().getAnnotation(annotation);
        if (null == ann) {
            throw new IllegalArgumentException(
                    String.format(
                            "Annotation %s required on class %s", annotation.getSimpleName(), from.getClass()));
        }
        return ann;
    }

    public static String formatEndpointId(ConnectorType type, String scenarioID, String connectorId) {
        return String.format(ENDPOINT_ID_FORMAT, type.getValue(), scenarioID, connectorId);
    }
}
