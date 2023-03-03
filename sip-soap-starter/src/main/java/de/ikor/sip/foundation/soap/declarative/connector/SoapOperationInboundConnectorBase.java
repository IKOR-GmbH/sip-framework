package de.ikor.sip.foundation.soap.declarative.connector;

import de.ikor.sip.foundation.core.declarative.connector.GenericInboundConnectorBase;
import java.util.Optional;
import java.util.function.Consumer;
import org.apache.camel.builder.DataFormatClause;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.builder.endpoint.dsl.DirectEndpointBuilderFactory;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;

/**
 * Base class for SOAP inbound connectors.
 *
 * <p>This class allows to bind <em>one</em> operation of a SOAP service interface to the
 * integration scenario of the connector.
 *
 * <p>SIP will automatically create a SOAP service for the provided service interface and initiate a
 * route that will invoke this connector only if the specified operation/method is called. It is
 * possible to create multiple connectors that use the same service interface. However, no more than
 * one connector can be specified for the same service and operation pair.
 *
 * <p>The base implementation will bind JAXB marshallers automatically.
 *
 * <p>In standard cases, adapter developers need only to implement {@link
 * #getServiceInterfaceClass()} and {@link #getServiceOperationName()}.
 *
 * @param <T> Service interface type
 */
public abstract class SoapOperationInboundConnectorBase<T> extends GenericInboundConnectorBase {

  @Override
  protected Optional<Consumer<DataFormatClause<ProcessorDefinition<RouteDefinition>>>>
      defineRequestUnmarshalling() {
    return Optional.of(unmarshaller -> unmarshaller.jaxb(getJaxbContextPathForRequestModel()));
  }

  /**
   * Returns the JAXB context path for the request model.
   *
   * @return Context path - defaults to the package name of the request model class
   */
  protected String getJaxbContextPathForRequestModel() {
    return getRequestModelClass().getPackageName();
  }

  @Override
  protected Optional<Consumer<DataFormatClause<ProcessorDefinition<RouteDefinition>>>>
      defineResponseMarshalling() {
    return getJaxbContextPathForResponseModel()
        .map(contextPath -> marshaller -> marshaller.jaxb(contextPath));
  }

  /**
   * Returns the JAXB context path for the response model.
   *
   * @return Context path - defaults to the package name of the response model class
   */
  protected Optional<String> getJaxbContextPathForResponseModel() {
    return getResponseModelClass().map(Class::getPackageName);
  }

  @Override
  protected final EndpointConsumerBuilder defineInitiatingEndpoint() {
    return getSoapServiceTieInEndpoint();
  }

  /**
   * Returns the endpoint used for this operation of the SOAP service
   *
   * @return
   */
  public final DirectEndpointBuilderFactory.DirectEndpointBuilder getSoapServiceTieInEndpoint() {
    return StaticEndpointBuilders.direct(buildQueueName());
  }

  private String buildQueueName() {
    return String.format(
        "sip-soap-service-connector-%s-%s",
        getServiceInterfaceClass().getSimpleName(), getServiceOperationName());
  }

  /**
   * Returns the service interface class
   *
   * <p>This is typically the service interface generated from a WSDL resource
   *
   * @return Service interface class
   */
  public abstract Class<T> getServiceInterfaceClass();

  /**
   * Returns the name of the operation that should be bound to the connector/integration scenario
   *
   * @return Service operation name
   */
  public abstract String getServiceOperationName();
}
