package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;

import static de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper.formatEndpointId;

public abstract class GenericOutboundConnectorBase extends ConnectorBase<RouteDefinition>
        implements OutboundConnectorDefinition<ConnectorOrchestrationInfo, RouteDefinition> {

    private final OutboundConnector outboundConnectorAnnotation =
            DeclarativeHelper.getAnnotationOrThrow(OutboundConnector.class, this);

    private final String connectorId =
            StringUtils.defaultIfEmpty(
                    outboundConnectorAnnotation.connectorId(),
                    formatEndpointId(getConnectorType(), getScenarioId(), getConnectorGroupId()));

    @Override
    public final RouteDefinition defineOutboundEndpoints(final RouteDefinition routeDefinition) {
        return routeDefinition.to(defineOutgoingEndpoint());
    }

    protected abstract EndpointProducerBuilder defineOutgoingEndpoint();

    @Override
    public String getConnectorGroupId() {
        return outboundConnectorAnnotation.belongsToGroup();
    }

    @Override
    public Class<? extends RouteDefinition> getEndpointDefinitionTypeClass() {
        return RouteDefinition.class;
    }

    @Override
    public final String getScenarioId() {
        return outboundConnectorAnnotation.fromScenario();
    }

    @Override
    public final String getConnectorId() {
        return connectorId;
    }
}
