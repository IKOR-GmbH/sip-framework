package de.ikor.sip.foundation.core.declarative.utils;

import de.ikor.sip.foundation.core.actuator.declarative.model.RouteDeclarativeStructureInfo;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorType;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.lang.annotation.Annotation;
import java.util.Map;
import org.apache.camel.Endpoint;

public class DeclarativeHelper {

  public static final String CONNECTOR_ID_FORMAT = "%s-%s-%s";

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

  public static String formatConnectorId(
      ConnectorType type, String scenarioID, String connectorId) {
    return String.format(CONNECTOR_ID_FORMAT, type.getValue(), scenarioID, connectorId);
  }

  public static Map<String, Object> appendMetadata(Endpoint endpoint, Map<String, Object> details) {
    RoutesRegistry routesRegistry =
        endpoint
            .getCamelContext()
            .getRegistry()
            .lookupByNameAndType("routesRegistry", RoutesRegistry.class);
    if (routesRegistry != null) {
      RouteDeclarativeStructureInfo structureInfo =
          routesRegistry.getInfoFromEndpointURI(endpoint.getEndpointUri());
      if (structureInfo != null) {
        details.put("metadata", structureInfo);
      }
    }
    return details;
  }
}
