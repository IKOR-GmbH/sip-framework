package de.ikor.sip.foundation.core.declarative.utils;

import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorType;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.camel.Endpoint;

@UtilityClass
public class DeclarativeHelper {

  public static final String CONNECTOR_ID_FORMAT = "%s-%s-%s";

  public static <A extends Annotation> A getAnnotationOrThrow(Class<A> annotation, Object from) {
    var ann = from.getClass().getAnnotation(annotation);
    if (null == ann) {
      throw new SIPFrameworkInitializationException(
          String.format(
              "Annotation @%s required on class %s", annotation.getSimpleName(), from.getClass()));
    }
    return ann;
  }

  public static <A extends Annotation> Optional<A> getAnnotationIfPresent(
      Class<A> annotation, Object from) {
    return Optional.ofNullable(from.getClass().getAnnotation(annotation));
  }

  public static String formatConnectorId(
      ConnectorType type, String scenarioID, String connectorId) {
    return String.format(CONNECTOR_ID_FORMAT, type.getValue(), scenarioID, connectorId);
  }

  @SneakyThrows
  public static <T> T createInstance(Class<T> clazz, Object... parameters) {
    Class<?>[] params = Arrays.stream(parameters).map(Object::getClass).toArray(Class[]::new);
    return clazz.getConstructor(params).newInstance(parameters);
  }

  /**
   * Append metadata to existing map of details needed in health check
   *
   * @param endpoint Endpoint for which matadata is needed
   * @param details Map with details
   * @return Map with filled metadata details
   */
  public static Map<String, Object> appendMetadata(Endpoint endpoint, Map<String, Object> details) {
    getRoutesRegistry(endpoint)
        .ifPresent(
            routesRegistry ->
                details.put("metadata", routesRegistry.generateRouteInfoList(endpoint)));
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
}
