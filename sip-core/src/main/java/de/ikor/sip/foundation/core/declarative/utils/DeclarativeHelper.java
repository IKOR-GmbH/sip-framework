package de.ikor.sip.foundation.core.declarative.utils;

import de.ikor.sip.foundation.core.declarative.RouteRole;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorType;
import de.ikor.sip.foundation.core.declarative.model.ModelMapper;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.endpoint.dsl.JmsEndpointBuilderFactory;
import org.mapstruct.factory.Mappers;

@UtilityClass
public class DeclarativeHelper {

  public static final String CONNECTOR_ID_FORMAT = "%s-%s-%s";

  public static <A extends Annotation> A getAnnotationOrThrow(Class<A> annotation, Object from) {
    var ann = from.getClass().getAnnotation(annotation);
    if (null == ann) {
      throw SIPFrameworkInitializationException.initException(
          "Annotation @%s required on class %s", annotation.getSimpleName(), from.getClass());
    }
    return ann;
  }

  public static <A extends Annotation> Optional<A> getAnnotationIfPresent(
      Class<A> annotation, Object from) {
    return Optional.ofNullable(from.getClass().getAnnotation(annotation));
  }

  public static String formatConnectorId(
      ConnectorType type, String scenarioID, String connectorGroupID) {
    return String.format(CONNECTOR_ID_FORMAT, type.getValue(), scenarioID, connectorGroupID);
  }

  public static <T extends ModelMapper> T createMapperInstance(Class<T> clazz) {
    try {
      return Mappers.getMapper(clazz);
    } catch (RuntimeException e) {
      // swallow the exception, it's not a mapstruct mapper
      try {
        return createInstance(clazz);
      } catch (NoSuchMethodException ex) {
        throw SIPFrameworkInitializationException.initException(
            "Mapper %s needs to have a no-arg constructor, please define one.", clazz.getName());
      } catch (InvocationTargetException
          | InstantiationException
          | IllegalAccessException exception) {
        throw SIPFrameworkInitializationException.initException(
            exception, "SIP couldn't create a Mapper %s.", clazz.getName());
      }
    }
  }

  @SneakyThrows
  private static <T> T createInstance(Class<T> clazz, Object... parameters)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException,
          IllegalAccessException {
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

  public static EndpointConsumerBuilder resolveForbiddenEndpoint(
      EndpointConsumerBuilder endpointConsumerBuilder) {
    if (endpointConsumerBuilder instanceof JmsEndpointBuilderFactory.JmsEndpointBuilder)
      endpointConsumerBuilder.doSetProperty("bridgeErrorHandler", false);
    return endpointConsumerBuilder;
  }

  public static Method getMappingMethod(Class<?> clazz) {
    List<Method> candidateMethods =
        Arrays.stream(clazz.getDeclaredMethods())
            .filter(method -> method.getName().equals(ModelMapper.MAPPING_METHOD_NAME))
            .filter(method -> !method.isBridge())
            .filter(method -> method.getParameterTypes().length == 1)
            .toList();
    if (candidateMethods.size() != 1) {
      throw SIPFrameworkInitializationException.initException(
          "Failed to automatically resolve the model classes for the Mapper: %s. Please @Override the getSourceModelClass() and getTargetModelClass() methods",
          clazz.getName());
    }
    return candidateMethods.get(0);
  }

  public static Class<?> getClassFromGeneric(Class<?> clazz, Class<?> abstractSuperclass) {
    return (Class<?>) traverseHierarchyTree(clazz, abstractSuperclass).getActualTypeArguments()[0];
  }

  private static ParameterizedType traverseHierarchyTree(Class<?> clazz, Class<?> superClass) {
    if (clazz.getSuperclass().equals(superClass)) {
      return (ParameterizedType) clazz.getGenericSuperclass();
    } else {
      return traverseHierarchyTree(clazz.getSuperclass(), superClass);
    }
  }

  public static boolean isPrimaryEndpoint(ConnectorType type, String role) {
    return isInboundPrimaryEndpoint(type, role) || isOutboundPrimaryEndpoint(type, role);
  }

  private static boolean isInboundPrimaryEndpoint(ConnectorType type, String role) {
    return type.equals(ConnectorType.IN)
        && (role.equals(RouteRole.EXTERNAL_ENDPOINT.getExternalName())
            || role.equals(RouteRole.EXTERNAL_SOAP_SERVICE_PROXY.getExternalName()));
  }

  private static boolean isOutboundPrimaryEndpoint(ConnectorType type, String role) {
    return type.equals(ConnectorType.OUT)
        && (role.equals(RouteRole.EXTERNAL_ENDPOINT.getExternalName()));
  }
}
