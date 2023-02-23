package de.ikor.sip.foundation.core.declarative.connector;

import static de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper.formatConnectorId;

import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import java.util.Optional;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;

/**
 * Base class for defining generic outbound connectors.
 *
 * <p>Adapter developers should extend this class and annotate it with @{@link OutboundConnector} to
 * specify the connector. The configuration of the outbound endpoint is done by overriding {@link
 * #defineOutgoingEndpoint()}.
 *
 * @see ConnectorBase#defineTransformationOrchestrator() Infos on attaching transformation between
 *     domain models of connector and integration scenario
 * @see OutboundConnector
 */
public abstract non-sealed class GenericOutboundConnectorBase extends ConnectorBase
    implements OutboundConnectorDefinition {

  private final OutboundConnector outboundConnectorAnnotation =
      DeclarativeHelper.getAnnotationOrThrow(OutboundConnector.class, this);

  private final String connectorId =
      StringUtils.defaultIfEmpty(
          outboundConnectorAnnotation.connectorId(),
          formatConnectorId(getConnectorType(), getScenarioId(), getConnectorGroupId()));

  @Override
  public final void defineOutboundEndpoints(final RouteDefinition routeDefinition) {
    routeDefinition.to(defineOutgoingEndpoint()).id(routeDefinition.getRouteId());
  }

  /**
   * Defines the outgoing endpoint for this connector.
   *
   * @see org.apache.camel.builder.endpoint.StaticEndpointBuilders
   * @see org.apache.camel.builder.endpoint.dsl.FileEndpointBuilderFactory.FileEndpointBuilder
   * @return the outgoing endpoint
   */
  protected abstract EndpointProducerBuilder defineOutgoingEndpoint();

  @Override
  public final String fromScenarioId() {
    return outboundConnectorAnnotation.fromScenario();
  }

  @Override
  public final String getId() {
    return connectorId;
  }

  @Override
  public String getConnectorGroupId() {
    return outboundConnectorAnnotation.belongsToGroup();
  }

  @Override
  public final Class<?> getRequestModelClass() {
    return outboundConnectorAnnotation.requestModel();
  }

  @Override
  public final Optional<Class<?>> getResponseModelClass() {
    var clazz = outboundConnectorAnnotation.responseModel();
    return clazz.equals(Void.class) ? Optional.empty() : Optional.of(clazz);
  }

  @Override
  public String getPathToDocumentationResource() {
    return outboundConnectorAnnotation.pathToDocumentationResource();
  }
}
