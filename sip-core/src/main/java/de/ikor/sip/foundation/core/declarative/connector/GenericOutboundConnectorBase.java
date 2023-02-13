package de.ikor.sip.foundation.core.declarative.connector;

import static de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper.formatConnectorId;

import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import java.util.Optional;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;

public abstract class GenericOutboundConnectorBase extends ConnectorBase
    implements OutboundConnectorDefinition {

  private final OutboundConnector outboundConnectorAnnotation =
      DeclarativeHelper.getAnnotationOrThrow(OutboundConnector.class, this);

  private final String connectorId =
      StringUtils.defaultIfEmpty(
          outboundConnectorAnnotation.connectorId(),
          formatConnectorId(getConnectorType(), getScenarioId(), getConnectorGroupId()));

  @Override
  public final void defineOutboundEndpoints(final RouteDefinition routeDefinition) {
    EndpointProducerBuilder out = defineOutgoingEndpoint();

    routeDefinition.to(out);
  }

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
  public final String getEndpointUri() {
    return defineOutgoingEndpoint().getUri();
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
