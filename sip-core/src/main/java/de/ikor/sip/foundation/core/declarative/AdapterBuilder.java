package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.endpoints.InboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.OutboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.RestEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.ResponseEndpoint;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestation.RestEndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.declarative.validator.CDMValidator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AdapterBuilder extends RouteBuilder {

  private static final String SIPMC = "sipmc:";

  private final ApplicationContext context;
  private final DeclarationsRegistry declarationsRegistry;

  private Map<IntegrationScenarioDefinition, List<InboundEndpointDefinition>> inboundEndpoints;
  private Map<IntegrationScenarioDefinition, List<OutboundEndpointDefinition>> outboundEndpoints;

  @PostConstruct
  private void fetchEndpoints() {
    this.inboundEndpoints =
        context.getBeansOfType(InboundEndpointDefinition.class).values().stream()
            .collect(
                Collectors.groupingBy(IntegrationScenarioProviderDefinition::getProvidedScenario));
    this.outboundEndpoints =
        context.getBeansOfType(OutboundEndpointDefinition.class).values().stream()
            .collect(
                Collectors.groupingBy(IntegrationScenarioConsumerDefinition::getConsumedScenario));
  }

  @Override
  public void configure() throws Exception {
    declarationsRegistry.getScenarios().values().forEach(this::buildScenario);
  }

  private void buildScenario(IntegrationScenarioDefinition scenarioDefinition) {
    List<InboundEndpointDefinition> inboundEndpointDefinitions =
        inboundEndpoints.get(scenarioDefinition);
    inboundEndpointDefinitions.forEach(
        definition -> buildInboundEndpoint(definition, scenarioDefinition));

    List<OutboundEndpointDefinition> outboundEndpointDefinitions =
        outboundEndpoints.get(scenarioDefinition);

    outboundEndpointDefinitions.forEach(
        definition -> buildOutboundEndpoint(definition, scenarioDefinition));
  }

  private void buildInboundEndpoint(
      InboundEndpointDefinition inboundEndpointDefinition,
      IntegrationScenarioDefinition scenarioDefinition) {

    RouteDefinition camelRoute = from(inboundEndpointDefinition.getInboundEndpoint());
    EndpointOrchestrationInfo orchestrationInfo =
        createInEndpointOrchestrationInfo(inboundEndpointDefinition, camelRoute);
    orchestrateEndpoint(orchestrationInfo, inboundEndpointDefinition);

    appendCDMValidation(scenarioDefinition.getRequestModelClass(), camelRoute);
    camelRoute.to(SIPMC + scenarioDefinition.getID());

    String routeId =
        String.format(
            "in-%s-%s", inboundEndpointDefinition.getConnectorId(), scenarioDefinition.getID());
    camelRoute.routeId(routeId);
    appendAfterHandler(orchestrationInfo.getRouteDefinition(), inboundEndpointDefinition);
  }

  private void buildOutboundEndpoint(
      OutboundEndpointDefinition outboundEndpointDefinition,
      IntegrationScenarioDefinition scenarioDefinition) {

    RouteDefinition camelRoute = from(SIPMC + scenarioDefinition.getID());
    EndpointOrchestrationInfo orchestrationInfo = () -> camelRoute;
    orchestrateEndpoint(orchestrationInfo, outboundEndpointDefinition);

    camelRoute.to(outboundEndpointDefinition.getOutboundEndpoint());

    scenarioDefinition
        .getResponseModelClass()
        .ifPresent(responseModelClass -> appendCDMValidation(responseModelClass, camelRoute));

    String routeId =
        String.format(
            "out-%s-%s", outboundEndpointDefinition.getConnectorId(), scenarioDefinition.getID());
    camelRoute.routeId(routeId);
    appendAfterHandler(orchestrationInfo.getRouteDefinition(), outboundEndpointDefinition);
  }

  private void appendCDMValidation(Class<?> CDMClass, RouteDefinition camelRoute) {
    camelRoute.process(new CDMValidator(CDMClass));
  }

  private void orchestrateEndpoint(
      EndpointOrchestrationInfo orchestrationInfo,
      Orchestratable<EndpointOrchestrationInfo> orchestratable) {

    Orchestrator<EndpointOrchestrationInfo> orchestrator = orchestratable.getOrchestrator();
    if (orchestrator.canOrchestrate(orchestrationInfo)) {
      orchestrator.doOrchestrate(orchestrationInfo);
    }
  }

  private EndpointOrchestrationInfo createInEndpointOrchestrationInfo(
      InboundEndpointDefinition inboundEndpointDefinition, RouteDefinition camelRoute) {

    if (inboundEndpointDefinition instanceof RestEndpoint) {
      RestDefinition restDefinition = rest();
      return new RestEndpointOrchestrationInfo() {
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

  private void appendAfterHandler(
      RouteDefinition routeDefinition, ResponseEndpoint responseHandler) {
    responseHandler.configureAfterResponse(routeDefinition);
  }
}
