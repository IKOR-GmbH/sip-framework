package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.OptionalIdentifiedDefinition;

/**
 * Definition of an <em>inbound</em> connector, which is a specialization of a {@link
 * ConnectorDefinition} that initiates an integration call for the integration scenario the
 * connector belongs to.
 *
 * <p>The inbound connector is responsible for defining the endpoint that initiates the integration
 * call. The endpoint is defined by the {@link #defineInboundEndpoints(OptionalIdentifiedDefinition,
 * EndpointProducerBuilder, RoutesRegistry)} method.
 *
 * <p>Adapter developers should not implement this interface directly, but rather extend one of the
 * inbound {@link ConnectorBase} subclasses and annotate it with @{@link
 * de.ikor.sip.foundation.core.declarative.annonation.InboundConnector}.
 *
 * @param <T> Type that is required by the inbound connector to define the endpoint. This is usually
 *     a {@link org.apache.camel.model.RoutesDefinition}.
 * @see ConnectorDefinition
 * @see GenericInboundConnectorBase
 * @see RestConnectorBase
 * @see de.ikor.sip.foundation.core.declarative.annonation.InboundConnector
 */
public interface InboundConnectorDefinition<T extends OptionalIdentifiedDefinition<T>>
    extends ConnectorDefinition, IntegrationScenarioProviderDefinition {

  /**
   * Defines the inbound endpoint(s) for the integration scenario the connector belongs to.
   *
   * @param definition Type that is required to initiate the endpoint.
   * @param targetToDefinition Target that the inbound endpoint should be connected to (e.g. the
   *     final {@link org.apache.camel.model.ProcessorDefinition#to(EndpointProducerBuilder)} in a
   *     {@link org.apache.camel.model.RouteDefinition}).
   * @param routeRegistry Route registry that must be used to register routeIds for the inbound
   *     endpoint(s).
   */
  void defineInboundEndpoints(
      T definition, EndpointProducerBuilder targetToDefinition, RoutesRegistry routeRegistry);

  @Override
  default ConnectorType getConnectorType() {
    return ConnectorType.IN;
  }

  @Override
  default String getScenarioId() {
    return toScenarioId();
  }

  /**
   * Returns the class of the type that is required to define the inbound endpoint(s).
   *
   * @return Class of the type that is required to define the inbound endpoint(s).
   */
  Class<? extends T> getEndpointDefinitionTypeClass();
}
