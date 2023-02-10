package de.ikor.sip.foundation.core.declarative.connector;

import static de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper.formatConnectorId;

import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import java.util.List;
import java.util.Optional;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.ToDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestsDefinition;
import org.apache.commons.lang3.StringUtils;

public abstract class RestConnectorBase extends ConnectorBase
    implements InboundConnectorDefinition<RestsDefinition> {

  private static final String REST_DIRECT_PATH = "rest";

  private final InboundConnector inboundConnectorAnnotation =
      DeclarativeHelper.getAnnotationOrThrow(InboundConnector.class, this);

  private final String connectorId =
      StringUtils.defaultIfEmpty(
          inboundConnectorAnnotation.connectorId(),
          formatConnectorId(getConnectorType(), getScenarioId(), getConnectorGroupId()));

  @Override
  public List<RouteDefinition> defineInboundEndpoints(
      final RestsDefinition definition, final EndpointProducerBuilder targetToDefinition) {
    var rest = definition.rest();
    configureRest(rest);
    rest.getVerbs()
        .forEach(verbDefinition -> verbDefinition.setTo(new ToDefinition(targetToDefinition)));
    return rest.asRouteDefinition(definition.getCamelContext());
  }

  protected abstract void configureRest(final RestDefinition definition);

  @Override
  public final Class<RestsDefinition> getEndpointDefinitionTypeClass() {
    return RestsDefinition.class;
  }

  @Override
  public String toScenarioId() {
    return inboundConnectorAnnotation.toScenario();
  }

  @Override
  public final String getConnectorId() {
    return connectorId;
  }

  @Override
  public final String getConnectorGroupId() {
    return inboundConnectorAnnotation.belongsToGroup();
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
}
