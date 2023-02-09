package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.validator.CDMValidator;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.model.rest.RestsDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class AdapterBuilder extends RouteBuilder {

    private static final String SCENARIO_HANDOVER_COMPONENT = "sipmc";
    private final DeclarationsRegistry declarationsRegistry;
    private final RoutesRegistry routesRegistry;
    private Map<IntegrationScenarioDefinition, List<InboundConnectorDefinition>> inboundEndpoints;
    private Map<IntegrationScenarioDefinition, List<OutboundConnectorDefinition>> outboundEndpoints;

    @PostConstruct
    private void fetchEndpoints() {
        this.inboundEndpoints =
                declarationsRegistry.getInboundEndpoints().stream()
                        .collect(
                                Collectors.groupingBy(
                                        endpoint -> declarationsRegistry.getScenarioById(endpoint.toScenarioId())));
        this.outboundEndpoints =
                declarationsRegistry.getOutboundEndpoints().stream()
                        .collect(
                                Collectors.groupingBy(
                                        endpoint -> declarationsRegistry.getScenarioById(endpoint.fromScenarioId())));
    }

    @Override
    public void configure() throws Exception {
        declarationsRegistry.getScenarios().forEach(this::buildScenario);
    }

    private void buildScenario(final IntegrationScenarioDefinition scenarioDefinition) {
        inboundEndpoints.get(scenarioDefinition).forEach(connectorDefinition -> buildInboundConnector(connectorDefinition, scenarioDefinition));
        outboundEndpoints.get(scenarioDefinition).forEach(connectorDefinition -> buildOutboundConnector(connectorDefinition, scenarioDefinition));
    }

    private void buildInboundConnector(
            final InboundConnectorDefinition inboundConnector,
            final IntegrationScenarioDefinition scenarioDefinition) {

        final var hasResponseRoute = scenarioDefinition.getResponseModelClass().isPresent();
        final var requestOrchestrationRouteId = routesRegistry.generateRouteIdForConnector(RouteRole.CONNECTOR_REQUEST_ORCHESTRATION, inboundConnector);
        final var responseOrchestrationRouteId = routesRegistry.generateRouteIdForConnector(RouteRole.CONNECTOR_RESPONSE_ORCHESTRATION, inboundConnector);
        final var handoffOrchestrationRouteId = routesRegistry.generateRouteIdForConnector(RouteRole.SCENARIO_HANDOFF, inboundConnector);

        // Channel all inbound camel endpoint routes to the orchestration route
        final var endpointDefinitionType = inboundConnector.getEndpointDefinitionTypeClass();
        final List<RouteDefinition> baseRoutes = inboundConnector.defineInboundEndpoints(resolveConnectorDefinitionType(endpointDefinitionType));
        var endpointCounter = 0;
        for (RouteDefinition baseRoute : baseRoutes) {
            baseRoute
                    .routeId(routesRegistry.generateRouteIdForConnector(RouteRole.EXTERNAL_ENDPOINT, inboundConnector, ++endpointCounter))
                    .to(StaticEndpointBuilders.direct(requestOrchestrationRouteId));
        }

        // Build scenario handoff and response-route
        final var handoffRouteDefinition = from(StaticEndpointBuilders.direct(handoffOrchestrationRouteId))
                .process(new CDMValidator(scenarioDefinition.getRequestModelClass()))                   // TODO: move validation to camel-component for scenarios
                .to(StaticEndpointBuilders.direct(SCENARIO_HANDOVER_COMPONENT, scenarioDefinition.getID()));
        if (hasResponseRoute) {
            handoffRouteDefinition.to(StaticEndpointBuilders.direct(responseOrchestrationRouteId));
        }

        // Build orchestration route(s) to/from scenario
        final var requestRouteDefinition = from(StaticEndpointBuilders.direct(requestOrchestrationRouteId))
                .routeId(requestOrchestrationRouteId);
        final Optional<RouteDefinition> responseRouteDefinition = hasResponseRoute ? Optional.of(from(StaticEndpointBuilders.direct(responseOrchestrationRouteId))
                .routeId(responseOrchestrationRouteId)) : Optional.empty();

        var orchestrationInfo = new OrchestrationRoutes(requestRouteDefinition, responseRouteDefinition);
        if (inboundConnector.getOrchestrator().canOrchestrate(orchestrationInfo)) {
            inboundConnector.getOrchestrator().doOrchestrate(orchestrationInfo);
        }
        requestRouteDefinition.to(StaticEndpointBuilders.direct(handoffOrchestrationRouteId));
    }

    private void buildOutboundConnector(
            final OutboundConnectorDefinition outboundEndpointDefinition,
            final IntegrationScenarioDefinition scenarioDefinition) {

        log.warn("Outbound connector initialization WIP");

//        RouteDefinition camelRoute = from(SCENARIO_HANDOVER_COMPONENT + scenarioDefinition.getID());
//        camelRoute.routeId(outboundEndpointDefinition.getConnectorId());
//        ConnectorOrchestrationInfo orchestrationInfo = () -> camelRoute;
//        orchestrateEndpoint(orchestrationInfo, outboundEndpointDefinition);
//
//        camelRoute.to(outboundEndpointDefinition.getOutboundEndpoint());
//        camelRoute.id(outboundEndpointDefinition.getConnectorId());
//        scenarioDefinition
//                .getResponseModelClass()
//                .ifPresent(responseModelClass -> appendCDMValidation(responseModelClass, camelRoute));
//        appendAfterHandler(orchestrationInfo.getRouteDefinition(), outboundEndpointDefinition);
    }


    private <T extends OptionalIdentifiedDefinition<T>> T resolveConnectorDefinitionType(Class<? extends T> type) {
        if (type.equals(RoutesDefinition.class)) {
            return (T) getRouteCollection();
        }
        if (type.equals(RestsDefinition.class)) {
            return (T) getRestCollection();
        }
        throw new SIPFrameworkInitializationException(String.format("Failed to resolve unknown connector definition type: %s", type.getName()));
    }

    private void orchestrateEndpoint(
            ConnectorOrchestrationInfo orchestrationInfo,
            Orchestratable<ConnectorOrchestrationInfo> orchestratable) {

        Orchestrator<ConnectorOrchestrationInfo> orchestrator = orchestratable.getOrchestrator();
        if (orchestrator.canOrchestrate(orchestrationInfo)) {
            orchestrator.doOrchestrate(orchestrationInfo);
        }
    }

    @Value
    private static class OrchestrationRoutes implements ConnectorOrchestrationInfo {
        RouteDefinition requestRouteDefinition;
        Optional<RouteDefinition> responseRouteDefinition;
    }
}
