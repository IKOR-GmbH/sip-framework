package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.RestConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestsDefinition;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static de.ikor.sip.foundation.core.declarative.utils.DeclarativeHelper.formatEndpointId;

public abstract class RestConnectorBase extends ConnectorBase<RestsDefinition>
        implements InboundConnectorDefinition<ConnectorOrchestrationInfo, RestsDefinition> {

    private static final String REST_DIRECT_PATH = "rest";

    private final InboundConnector inboundConnectorAnnotation =
            DeclarativeHelper.getAnnotationOrThrow(InboundConnector.class, this);

    private final String connectorId =
            StringUtils.defaultIfEmpty(
                    inboundConnectorAnnotation.connectorId(),
                    formatEndpointId(getConnectorType(), getScenarioId(), getConnectorGroupId()));

    @Override
    public List<RouteDefinition> defineInboundEndpoints(final RestsDefinition definition) {
        var rest = definition.rest();
        configureRest(rest);
        return rest.asRouteDefinition(definition.getCamelContext());
    }

    protected abstract void configureRest(final RestDefinition definition);

    @Override
    public final String getConnectorGroupId() {
        return inboundConnectorAnnotation.belongsToGroup();
    }

    @Override
    public final Class<RestsDefinition> getEndpointDefinitionTypeClass() {
        return RestsDefinition.class;
    }

    @Override
    public final String getScenarioId() {
        return inboundConnectorAnnotation.toScenario();
    }

    @Override
    public final String getConnectorId() {
        return connectorId;
    }

    private void prependRestRoute(RestConnectorOrchestrationInfo orchestrationInfo) {
      /*  RestDefinition restDefinition = orchestrationInfo.getRestDefinition();
        if (!restDefinition.getVerbs().isEmpty()) {
            restDefinition
                    .getVerbs()
                    .forEach(
                            verbDefinition -> {
                                verbDefinition.setTo(new ToDefinition(getInboundEndpoint().getUri()));
                                verbDefinition.setId(
                                        verbDefinition.asVerb()
                                                + "-"
                                                + verbDefinition.getPath()
                                                + "-"
                                                + getConnectorId());
                            });
        }*/
    }
}
