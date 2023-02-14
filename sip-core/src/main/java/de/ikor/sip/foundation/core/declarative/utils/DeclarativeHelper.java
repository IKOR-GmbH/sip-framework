package de.ikor.sip.foundation.core.declarative.utils;

import de.ikor.sip.foundation.core.actuator.declarative.model.RouteDeclarativeStructureInfo;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorType;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.lang.annotation.Annotation;
import java.util.*;
import org.apache.camel.Endpoint;
import org.apache.camel.Route;
import org.apache.camel.Service;
import org.apache.camel.processor.SendProcessor;

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
    Set<String> routeIds = findRoutes(endpoint);
    if (routeIds.isEmpty()) {
      return details;
    }
    getRoutesRegistry(endpoint)
        .ifPresent(
            routesRegistry -> {
              List<RouteDeclarativeStructureInfo> structureInfo = new ArrayList<>();
              routeIds.forEach(
                  routeId -> structureInfo.add(routesRegistry.generateRouteInfo(routeId)));
              details.put("metadata", structureInfo);
            });
    return details;
  }

  private static Optional<RoutesRegistry> getRoutesRegistry(Endpoint endpoint) {
    RoutesRegistry routesRegistry =
        endpoint
            .getCamelContext()
            .getRegistry()
            .lookupByNameAndType("routesRegistry", RoutesRegistry.class);
    return Optional.ofNullable(routesRegistry);
  }

  private static Set<String> findRoutes(Endpoint endpoint) {
    Set<String> routeIds = new HashSet<>();
    for (Route route : endpoint.getCamelContext().getRoutes()) {
      if (endpoint.equals(route.getEndpoint())) {
        routeIds.add(route.getRouteId());
      }
      for (Service service : route.getServices()) {
        if (service instanceof SendProcessor
            && ((SendProcessor) service).getEndpoint().equals(endpoint)) {
          routeIds.add(route.getRouteId());
        }
      }
    }
    return routeIds;
  }
}
