package de.ikor.sip.foundation.core.framework.endpoints;

import lombok.Getter;

import java.util.Optional;
import java.util.function.Function;

public class InEndpoint {

  @Getter private String uri;
  @Getter private String id;
  @Getter private Optional<Class<?>> domainCLassType;
  @Getter private Optional<Function<?, ?>> transformFunction;

  public static InEndpoint instance(String uri, String id) {
    InEndpoint inEndpoint = new InEndpoint(uri, id);
    return putInRegister(inEndpoint, id);
  }

  public static InEndpoint instance(String uri, String id, Class<?> domainCLassType) {
    InEndpoint inEndpoint = new InEndpoint(uri, id);
    inEndpoint.setDomainCLassType(domainCLassType);
    return putInRegister(inEndpoint, id);
  }

  public static <T, D> InEndpoint instance(String uri, String id, Class<D> domainCLassType, Function<T, D> transformFunction) {
    InEndpoint inEndpoint = new InEndpoint(uri, id);
    inEndpoint.setDomainCLassType(domainCLassType);
    inEndpoint.setTransformFunction(transformFunction);
    return putInRegister(inEndpoint, id);
  }

  protected InEndpoint(String uri, String id) {
    this.uri = uri;
    this.id = id;
    this.domainCLassType = Optional.empty();
    this.transformFunction = Optional.empty();
  }

  private static InEndpoint putInRegister(InEndpoint inEndpoint, String id) {
    CentralEndpointsRegister.put(id, inEndpoint);
    return CentralEndpointsRegister.getInEndpoint(id);
  }

  private void setDomainCLassType(Class<?> domainCLassType) {
    this.domainCLassType = Optional.of(domainCLassType);
  }

  private void setTransformFunction(Function<?, ?> transformFunction) {
    this.transformFunction = Optional.of(transformFunction);
  }

}
