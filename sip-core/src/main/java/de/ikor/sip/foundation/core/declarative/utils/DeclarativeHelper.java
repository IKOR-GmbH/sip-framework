package de.ikor.sip.foundation.core.declarative.utils;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorType;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.lang.annotation.Annotation;

public class DeclarativeHelper {
<<<<<<< Updated upstream
    public static final String CONNECTOR_ID_FORMAT = "%s-%s-%s";
=======
  public static final String ENDPOINT_ID_FORMAT = "%s-%s-%s";
>>>>>>> Stashed changes

  private DeclarativeHelper() {}

  public static <A extends Annotation> A getAnnotationOrThrow(Class<A> annotation, Object from) {
    var ann = from.getClass().getAnnotation(annotation);
    if (null == ann) {
      throw new SIPFrameworkInitializationException(
          String.format(
              "Annotation @%s required on class %s", annotation.getSimpleName(), from.getClass()));
    }
    return ann;
  }

<<<<<<< Updated upstream
    public static String formatConnectorId(ConnectorType type, String scenarioID, String connectorId) {
        return String.format(CONNECTOR_ID_FORMAT, type.getValue(), scenarioID, connectorId);
    }
=======
  public static String formatEndpointId(ConnectorType type, String scenarioID, String connectorId) {
    return String.format(ENDPOINT_ID_FORMAT, type.getValue(), scenarioID, connectorId);
  }
>>>>>>> Stashed changes
}
