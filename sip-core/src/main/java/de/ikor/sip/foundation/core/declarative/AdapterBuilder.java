package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.endpoints.InboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.OutboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.RestEndpoint;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestation.RestEndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AdapterBuilder extends RouteBuilder {

  private static final String DIRECT_REST = "direct:rest";
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
    for (InboundEndpointDefinition definition : inboundEndpointDefinitions) {
      buildInboundEndpoint(definition, scenarioDefinition.getID());
    }

    List<OutboundEndpointDefinition> outboundEndpointDefinitions =
        outboundEndpoints.get(scenarioDefinition);
    for (OutboundEndpointDefinition definition : outboundEndpointDefinitions) {
      buildOutboundEndpoint(definition, scenarioDefinition.getID());
    }
  }

  private void buildInboundEndpoint(
      InboundEndpointDefinition inboundEndpointDefinition, String scenarioID) {
    if (inboundEndpointDefinition instanceof RestEndpoint) {
      buildRestEndpoint((RestEndpoint) inboundEndpointDefinition, scenarioID);
      return;
    }
    RouteDefinition camelRoute = from(inboundEndpointDefinition.getInboundEndpoint());
    EndpointOrchestrationInfo orchestrationInfo = () -> camelRoute;
    orchestrateEndpoint(orchestrationInfo, inboundEndpointDefinition);

    camelRoute.to(SIPMC + scenarioID);
    String routeId =
        String.format(
            "in-%s-%s",
            inboundEndpointDefinition.getConnectorId(), scenarioID);
    camelRoute.routeId(routeId);

  }

  private void buildOutboundEndpoint(
      OutboundEndpointDefinition outboundEndpointDefinition, String scenarioID) {

    RouteDefinition camelRoute = from(SIPMC + scenarioID);
    EndpointOrchestrationInfo orchestrationInfo = () -> camelRoute;
    orchestrateEndpoint(orchestrationInfo, outboundEndpointDefinition);
    camelRoute.to(outboundEndpointDefinition.getOutboundEndpoint());
    String routeId =
        String.format(
            "out-%s-%s",
            outboundEndpointDefinition.getConnectorId(), scenarioID);
    camelRoute.routeId(routeId);
  }

  private void buildRestEndpoint(RestEndpoint restEndpointDefinition, String scenarioID) {

    RestDefinition restRoute = rest();
    RouteDefinition camelRoute = from(DIRECT_REST + scenarioID);

    RestEndpointOrchestrationInfo restEndpointBridgeInfo =
        new RestEndpointOrchestrationInfo() {
          @Override
          public RestDefinition getRestDefinition() {
            return restRoute;
          }

          @Override
          public RouteDefinition getRouteDefinition() {
            return camelRoute;
          }
        };

    orchestrateEndpoint(restEndpointBridgeInfo, restEndpointDefinition);

    restRoute.to(DIRECT_REST + scenarioID);
    camelRoute.to(SIPMC + scenarioID);
  }

  private void orchestrateEndpoint(
      EndpointOrchestrationInfo orchestrationInfo,
      Orchestratable<EndpointOrchestrationInfo> orchestratable) {

    Orchestrator<EndpointOrchestrationInfo> orchestrator = orchestratable.getOrchestrator();
    if (orchestrator.canOrchestrate(orchestrationInfo)) {
      orchestrator.doOrchestrate(orchestrationInfo);
    }
  }
}
