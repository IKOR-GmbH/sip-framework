package de.ikor.sip.foundation.core.declarative.connector;

import static de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper.formatConnectorId;
import static de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper.resolveForbiddenEndpoint;

import de.ikor.sip.foundation.core.declarative.RouteRole;
import de.ikor.sip.foundation.core.declarative.RoutesRegistry;
import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.model.MarshallerDefinition;
import de.ikor.sip.foundation.core.declarative.model.UnmarshallerDefinition;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import java.util.Optional;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RoutesDefinition;
import org.apache.commons.lang3.StringUtils;

/**
 * Base class for defining generic inbound connectors.
 *
 * <p>Adapter developers should extend this class and annotate it with @{@link InboundConnector} to
 * specify the connector. The configuration of the inbound endpoint is done by overriding {@link
 * #defineInitiatingEndpoint()}.
 *
 * @see ConnectorBase#defineTransformationOrchestrator() Infos on attaching transformation between
 *     domain models of connector and integration scenario
 * @see InboundConnector
 */
public abstract class GenericInboundConnectorBase extends ConnectorBase
    implements InboundConnectorDefinition<RoutesDefinition> {

  private final InboundConnector inboundConnectorAnnotation =
      DeclarativeHelper.getAnnotationOrThrow(InboundConnector.class, this);

  private final String connectorId =
      StringUtils.defaultIfEmpty(
          inboundConnectorAnnotation.connectorId(),
          formatConnectorId(getConnectorType(), getScenarioId(), getConnectorGroupId()));

  @Override
  public final void defineInboundEndpoints(
      final RoutesDefinition definition,
      final EndpointProducerBuilder targetToDefinition,
      final RoutesRegistry routeRegistry) {
    final var routeDef =
        definition
            .from(resolveForbiddenEndpoint(defineInitiatingEndpoint()))
            .routeId(routeRegistry.generateRouteIdForConnector(RouteRole.EXTERNAL_ENDPOINT, this));
    defineRequestUnmarshalling().ifPresent(unmarshaller -> unmarshaller.accept(routeDef));
    routeDef.to(targetToDefinition);
    defineResponseMarshalling().ifPresent(marshaller -> marshaller.accept(routeDef));
  }

  /**
   * Handle meant to be overloaded if the definition of an unmarshaller for the request type is
   * needed.
   *
   * @return Unmarshaller for the request type
   */
  protected Optional<UnmarshallerDefinition> defineRequestUnmarshalling() {
    return Optional.empty();
  }

  /**
   * Handle meant to be overloaded if the definition of a marshaller for the response type is
   * needed.
   *
   * @return Marshaller for response type
   */
  protected Optional<MarshallerDefinition> defineResponseMarshalling() {
    return Optional.empty();
  }

  /**
   * Defines the initiating endpoint for this connector.
   *
   * @see org.apache.camel.builder.endpoint.StaticEndpointBuilders
   * @see org.apache.camel.builder.endpoint.dsl.FileEndpointBuilderFactory.FileEndpointBuilder
   * @return the initiating endpoint
   */
  protected abstract EndpointConsumerBuilder defineInitiatingEndpoint();

  @Override
  public final Class<RoutesDefinition> getEndpointDefinitionTypeClass() {
    return RoutesDefinition.class;
  }

  @Override
  public final String toScenarioId() {
    return inboundConnectorAnnotation.integrationScenario();
  }

  @Override
  public final ConnectorType getConnectorType() {
    return InboundConnectorDefinition.super.getConnectorType();
  }

  @Override
  public final String getId() {
    return connectorId;
  }

  @Override
  public final String getConnectorGroupId() {
    return inboundConnectorAnnotation.connectorGroup();
  }

  @Override
  public final Class<?> getRequestModelClass() {
    return inboundConnectorAnnotation.requestModel();
  }

  @Override
  public final Optional<Class<?>> getResponseModelClass() {
    var clazz = inboundConnectorAnnotation.responseModel();
    return clazz.equals(Void.class) ? Optional.empty() : Optional.of(clazz);
  }

  @Override
  public String getPathToDocumentationResource() {
    return inboundConnectorAnnotation.pathToDocumentationResource();
  }

  @Override
  public final String getScenarioId() {
    return InboundConnectorDefinition.super.getScenarioId();
  }
}
