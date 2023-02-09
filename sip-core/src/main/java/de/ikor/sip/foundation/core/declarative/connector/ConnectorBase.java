package de.ikor.sip.foundation.core.declarative.connector;

import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import lombok.Getter;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ConnectorBase<DEFINITION_TYPE extends OptionalIdentifiedDefinition<DEFINITION_TYPE>>
        implements ConnectorDefinition<ConnectorOrchestrationInfo, DEFINITION_TYPE> {

    @Getter
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Orchestrator<ConnectorOrchestrationInfo> orchestrator = buildConnectorOrchestrator();

    private Orchestrator<ConnectorOrchestrationInfo> buildConnectorOrchestrator() {
        return new Orchestrator<ConnectorOrchestrationInfo>() {
            @Override
            public boolean canOrchestrate(final ConnectorOrchestrationInfo data) {
                return true;
            }

            @Override
            public void doOrchestrate(final ConnectorOrchestrationInfo data) {
                ConnectorBase.this.defineRequestRoute(data.getRequestRouteDefinition());
                data.getResponseRouteDefinition().ifPresent(ConnectorBase.this::defineResponseRoute);
            }
        };
    }

    protected abstract void defineRequestRoute(final RouteDefinition definition);

    protected void defineResponseRoute(final RouteDefinition definition) {
        getLogger().warn("IntegrationScenario {} has a return type, but no response route has been defined in connector {}", getScenarioId(), getConnectorId());
    }

    public abstract String getScenarioId();

    public abstract String getConnectorId();

    public Orchestrator<ConnectorOrchestrationInfo> getOrchestrator() {
        return orchestrator;
    }

}
