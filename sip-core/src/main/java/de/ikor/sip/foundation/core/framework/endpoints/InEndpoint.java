package de.ikor.sip.foundation.core.framework.endpoints;

import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import org.apache.camel.builder.EndpointConsumerBuilder;

public class InEndpoint {

  @Getter private String uri;
  @Getter private String id;
  @Getter private Optional<Class<?>> domainClassType;
  @Getter private Optional<Function<?, ?>> transformFunction;

  public static InEndpoint instance(String uri, String id) {
    InEndpoint inEndpoint = new InEndpoint(uri, id);
    return putInRegister(inEndpoint, id);
  }

  public static InEndpoint instance(String uri, String id, Class<?> domainClassType) {
    InEndpoint inEndpoint = new InEndpoint(uri, id);
    inEndpoint.setDomainClassType(domainClassType);
    return putInRegister(inEndpoint, id);
  }

  public static <T, D> InEndpoint instance(
      String uri, String id, Class<D> domainClassType, Function<T, D> transformFunction) {
    InEndpoint inEndpoint = new InEndpoint(uri, id);
    inEndpoint.setDomainClassType(domainClassType);
    inEndpoint.setTransformFunction(transformFunction);
    return putInRegister(inEndpoint, id);
  }

  public static InEndpoint instance(EndpointConsumerBuilder endpointDslDefinition, String id) {
    return instance(endpointDslDefinition.getUri(), id);
  }

  public static InEndpoint instance(EndpointConsumerBuilder endpointDslDefinition, String id, Class<?> domainClassType) {
    return instance(endpointDslDefinition.getUri(), id, domainClassType);
  }

  public static <T, D> InEndpoint instance(EndpointConsumerBuilder endpointDslDefinition, String id, Class<D> domainClassType, Function<T, D> transformFunction) {
    return instance(endpointDslDefinition.getUri(), id, domainClassType, transformFunction);
  }

  protected InEndpoint(String uri, String id) {
    this.uri = uri;
    this.id = id;
    this.domainClassType = Optional.empty();
    this.transformFunction = Optional.empty();
  }

  private static InEndpoint putInRegister(InEndpoint inEndpoint, String id) {
    CentralEndpointsRegister.put(id, inEndpoint);
    return CentralEndpointsRegister.getInEndpoint(id);
  }

  private void setDomainClassType(Class<?> domainClassType) {
    this.domainClassType = Optional.ofNullable(domainClassType);
  }

  private void setTransformFunction(Function<?, ?> transformFunction) {
    this.transformFunction = Optional.ofNullable(transformFunction);
  }
}
