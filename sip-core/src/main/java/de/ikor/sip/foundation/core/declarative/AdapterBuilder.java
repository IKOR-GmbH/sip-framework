package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.RestConnectorBase;
import de.ikor.sip.foundation.core.declarative.orchestation.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestation.RestConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.validator.CDMValidator;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestsDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AdapterBuilder extends RouteBuilder {

    private static final String SIPMC = "sipmc:";
    private final DeclarationsRegistry declarationsRegistry;

    private Map<IntegrationScenarioDefinition, List<InboundConnectorDefinition>> inboundEndpoints;
    private Map<IntegrationScenarioDefinition, List<OutboundConnectorDefinition>> outboundEndpoints;

    @PostConstruct
    private void fetchEndpoints() {
        this.inboundEndpoints =
                declarationsRegistry.getInboundEndpoints().stream()
                        .collect(
                                Collectors.groupingBy(
                                        endpoint -> declarationsRegistry.getScenarioById(endpoint.getScenarioId())));
        this.outboundEndpoints =
                declarationsRegistry.getOutboundEndpoints().stream()
                        .collect(
                                Collectors.groupingBy(
                                        endpoint -> declarationsRegistry.getScenarioById(endpoint.getScenarioId())));
    }

    @Override
    public void configure() throws Exception {
        declarationsRegistry.getScenarios().forEach(this::buildScenario);
    }

    private void buildScenario(IntegrationScenarioDefinition scenarioDefinition) {
        List<InboundConnectorDefinition> inboundEndpointDefinitions =
                inboundEndpoints.get(scenarioDefinition);
        inboundEndpointDefinitions.forEach(
                definition -> buildInboundEndpoint(definition, scenarioDefinition));

        List<OutboundConnectorDefinition> outboundEndpointDefinitions =
                outboundEndpoints.get(scenarioDefinition);

        outboundEndpointDefinitions.forEach(
                definition -> buildOutboundEndpoint(definition, scenarioDefinition));
    }

    private void buildInboundEndpoint(
            InboundConnectorDefinition inboundEndpointDefinition,
            IntegrationScenarioDefinition scenarioDefinition) {


        RouteDefinition camelRoute = from(inboundEndpointDefinition.defineInboundEndpoints());
        camelRoute.routeId(inboundEndpointDefinition.getConnectorId());
        ConnectorOrchestrationInfo orchestrationInfo =
                createInEndpointOrchestrationInfo(inboundEndpointDefinition, camelRoute);
        orchestrateEndpoint(orchestrationInfo, inboundEndpointDefinition);

        appendCDMValidation(scenarioDefinition.getRequestModelClass(), camelRoute);
        camelRoute.to(SIPMC + scenarioDefinition.getID());
        camelRoute.id(inboundEndpointDefinition.getConnectorId());
        appendAfterHandler(orchestrationInfo.getRouteDefinition(), inboundEndpointDefinition);


    }

    private void buildOutboundEndpoint(
            OutboundConnectorDefinition outboundEndpointDefinition,
            IntegrationScenarioDefinition scenarioDefinition) {

        RouteDefinition camelRoute = from(SIPMC + scenarioDefinition.getID());
        camelRoute.routeId(outboundEndpointDefinition.getConnectorId());
        ConnectorOrchestrationInfo orchestrationInfo = () -> camelRoute;
        orchestrateEndpoint(orchestrationInfo, outboundEndpointDefinition);

        camelRoute.to(outboundEndpointDefinition.getOutboundEndpoint());
        camelRoute.id(outboundEndpointDefinition.getConnectorId());
        scenarioDefinition
                .getResponseModelClass()
                .ifPresent(responseModelClass -> appendCDMValidation(responseModelClass, camelRoute));
        appendAfterHandler(orchestrationInfo.getRouteDefinition(), outboundEndpointDefinition);
    }

    private ConnectorOrchestrationInfo createInEndpointOrchestrationInfo(
            InboundConnectorDefinition inboundEndpointDefinition, RouteDefinition camelRoute) {

        if (inboundEndpointDefinition instanceof RestConnectorBase) {
            RestDefinition restDefinition = rest();
            return new RestConnectorOrchestrationInfo() {
                @Override
                public RestDefinition getRestDefinition() {
                    return restDefinition;
                }

                @Override
                public RouteDefinition getRouteDefinition() {
                    return camelRoute;
                }
            };
        }
        return () -> camelRoute;
    }

    private void orchestrateEndpoint(
            ConnectorOrchestrationInfo orchestrationInfo,
            Orchestratable<ConnectorOrchestrationInfo> orchestratable) {

        Orchestrator<ConnectorOrchestrationInfo> orchestrator = orchestratable.getOrchestrator();
        if (orchestrator.canOrchestrate(orchestrationInfo)) {
            orchestrator.doOrchestrate(orchestrationInfo);
        }
    }

    private void appendCDMValidation(Class<?> CDMClass, RouteDefinition camelRoute) {
        camelRoute.process(new CDMValidator(CDMClass));
    }

    private void appendAfterHandler(
            RouteDefinition routeDefinition, ConnectorDefinition responseHandler) {
        responseHandler.configureAfterResponse(routeDefinition);
    }

    public <T> T getRequiredElement(Class<? extends T> type) {

        if (type.equals(RoutesDefinition.class)) {
            return (T) getRouteCollection();
        }

        if (type.equals(RestsDefinition.class)) {
            return (T) getRestCollection();
        }

        throw new SIPFrameworkInitializationException("Unknown connector initialization type: " + type.getName());
    }
}
