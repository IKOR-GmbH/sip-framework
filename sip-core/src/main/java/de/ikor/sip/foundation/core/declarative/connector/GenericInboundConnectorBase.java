package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper.formatEndpointId;

public abstract class GenericInboundConnectorBase extends ConnectorBase implements InboundConnectorDefinition<RoutesDefinition> {

    private final InboundConnector inboundConnectorAnnotation =
            DeclarativeHelper.getAnnotationOrThrow(InboundConnector.class, this);

    private final String connectorId =
            StringUtils.defaultIfEmpty(
                    inboundConnectorAnnotation.connectorId(),
                    formatEndpointId(getConnectorType(), getScenarioId(), getConnectorGroupId()));

    @Override
    public final List<RouteDefinition> defineInboundEndpoints(final RoutesDefinition definition, final String toRouteId) {
        return Collections.singletonList(definition.from(defineInitiatingEndpoint()).to(StaticEndpointBuilders.direct(toRouteId)));
    }

    protected abstract EndpointConsumerBuilder defineInitiatingEndpoint();

    @Override
    public final Class<RoutesDefinition> getEndpointDefinitionTypeClass() {
        return RoutesDefinition.class;
    }

    @Override
    public final String toScenarioId() {
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
