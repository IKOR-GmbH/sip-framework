package de.ikor.sip.foundation.core.framework.endpoints;

import static java.lang.String.format;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import org.apache.camel.*;
import org.apache.camel.builder.EndpointProducerBuilder;

public class OutEndpoint implements Endpoint {
  private final Endpoint targetEndpoint;
  @Getter private final String endpointId;
  @Getter private Optional<Class<?>> domainClassType;
  @Getter private Optional<Function<?, ?>> transformFunction;

  public static OutEndpoint instance(String uri, String endpointId) {
    OutEndpoint endpoint = new OutEndpoint(uri, endpointId);
    return putInRegister(endpoint, endpointId);
  }

  public static OutEndpoint instance(String uri, String endpointId, Class<?> domainClassType) {
    OutEndpoint endpoint = new OutEndpoint(uri, endpointId);
    endpoint.setDomainClassType(domainClassType);
    return putInRegister(endpoint, endpointId);
  }

  public static <T, D> OutEndpoint instance(
      String uri, String endpointId, Class<T> domainClassType, Function<T, D> transformFunction) {
    OutEndpoint endpoint = new OutEndpoint(uri, endpointId);
    endpoint.setDomainClassType(domainClassType);
    endpoint.setTransformFunction(transformFunction);
    return putInRegister(endpoint, endpointId);
  }

  public static OutEndpoint instance(
      EndpointProducerBuilder endpointDslDefinition, String endpointId) {
    return instance(endpointDslDefinition.getUri(), endpointId);
  }

  public static OutEndpoint instance(
      EndpointProducerBuilder endpointDslDefinition, String endpointId, Class<?> domainClassType) {
    return instance(endpointDslDefinition.getUri(), endpointId, domainClassType);
  }

  public static <T, D> OutEndpoint instance(
      EndpointProducerBuilder endpointDslDefinition,
      String endpointId,
      Class<T> domainClassType,
      Function<T, D> transformFunction) {
    return instance(endpointDslDefinition.getUri(), endpointId, domainClassType, transformFunction);
  }

  OutEndpoint(String uri, String endpointId) {
    this.targetEndpoint = CentralEndpointsRegister.getCamelEndpoint(uri);
    this.setCamelContext(targetEndpoint.getCamelContext());
    this.endpointId = endpointId;
    this.domainClassType = Optional.empty();
    this.transformFunction = Optional.empty();
  }

  private static OutEndpoint putInRegister(OutEndpoint endpoint, String endpointId) {
    CentralEndpointsRegister.put(endpointId, endpoint);
    return (OutEndpoint) CentralEndpointsRegister.getOutEndpoint(endpointId);
  }

  private void setDomainClassType(Class<?> domainClassType) {
    this.domainClassType = Optional.of(domainClassType);
  }

  private void setTransformFunction(Function<?, ?> transformFunction) {
    this.transformFunction = Optional.of(transformFunction);
  }

  @Override
  public Producer createProducer() throws Exception {
    return targetEndpoint.createProducer();
  }

  @Override
  public AsyncProducer createAsyncProducer() throws Exception {
    return targetEndpoint.createAsyncProducer();
  }

  @Override
  public Consumer createConsumer(Processor processor) throws Exception {
    throw new IllegalAccessException(
        format("%s should not utilize consumer", this.getClass().getName()));
  }

  @Override
  public PollingConsumer createPollingConsumer() throws Exception {
    throw new IllegalAccessException(
        format("%s should not utilize consumer", this.getClass().getName()));
  }

  @Override
  public void configureProperties(Map<String, Object> options) {
    targetEndpoint.configureProperties(options);
  }

  @Override
  public void setCamelContext(CamelContext context) {
    targetEndpoint.setCamelContext(context);
  }

  @Override
  public boolean isLenientProperties() {
    return false;
  }

  @Override
  public String getEndpointUri() {
    return targetEndpoint.getEndpointUri();
  }

  @Override
  public ExchangePattern getExchangePattern() {
    return targetEndpoint.getExchangePattern();
  }

  @Override
  public String getEndpointKey() {
    return targetEndpoint.getEndpointKey();
  }

  @Override
  public Exchange createExchange() {
    return null;
  }

  @Override
  public Exchange createExchange(ExchangePattern pattern) {
    return targetEndpoint.createExchange(pattern);
  }

  @Override
  public void configureExchange(Exchange exchange) {
    targetEndpoint.configureExchange(exchange);
  }

  @Override
  public CamelContext getCamelContext() {
    return targetEndpoint.getCamelContext();
  }

  @Override
  public boolean isSingleton() {
    return targetEndpoint.isSingleton();
  }

  @Override
  public void start() {
    targetEndpoint.start();
  }

  @Override
  public void stop() {
    targetEndpoint.stop();
  }
}