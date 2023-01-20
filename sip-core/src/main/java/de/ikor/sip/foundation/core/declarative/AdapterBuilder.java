package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.endpoints.InboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.OutboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.RestEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestation.RestEndpointBridgeInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
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

  private final ApplicationContext context;
  private final DeclarationsRegistry declarationsRegistry;

  private Map<IntegrationScenarioDefinition, List<InboundEndpointDefinition>> inboundEndpoints;
  private Map<IntegrationScenarioDefinition, List<OutboundEndpointDefinition>> outboundEndpoints;
  private Map<IntegrationScenarioDefinition, List<RestEndpointDefinition>> restEndpoints;

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
    this.restEndpoints =
            context.getBeansOfType(RestEndpointDefinition.class).values().stream()
                    .collect(
                            Collectors.groupingBy(IntegrationScenarioProviderDefinition::getProvidedScenario));
  }

  @Override
  public void configure() throws Exception {
    declarationsRegistry.getScenarios().values().forEach(this::buildScenario);
  }

  private void buildScenario(IntegrationScenarioDefinition scenarioDefinition) {
    List<InboundEndpointDefinition> inboundEndpointDefinitions =
        inboundEndpoints.get(scenarioDefinition);
    if(inboundEndpointDefinitions != null)
    for (InboundEndpointDefinition definition : inboundEndpointDefinitions) {
      buildInboundEndpoint(definition, scenarioDefinition.getID());
    }

    List<OutboundEndpointDefinition> outboundEndpointDefinitions =
        outboundEndpoints.get(scenarioDefinition);
    if(outboundEndpointDefinitions != null)
    for (OutboundEndpointDefinition definition : outboundEndpointDefinitions) {
      buildOutboundEndpoint(definition, scenarioDefinition.getID());
    }


    List<RestEndpointDefinition> restEndpointDefinitions =
            restEndpoints.get(scenarioDefinition);
    if(restEndpointDefinitions != null)
    for (RestEndpointDefinition definition : restEndpointDefinitions) {
      buildRestEndpoint(definition, scenarioDefinition.getID());
    }
  }

  private void buildInboundEndpoint(
      InboundEndpointDefinition inboundEndpointDefinition, String scenarioID) {
    RouteDefinition camelRoute = from(inboundEndpointDefinition.getInboundEndpoint());
    orchestrateEndpoint(camelRoute, inboundEndpointDefinition);
    camelRoute.to("sipmc:" + scenarioID);
  }

  private void buildOutboundEndpoint(
      OutboundEndpointDefinition outboundEndpointDefinition, String scenarioID) {

    RouteDefinition camelRoute = from("sipmc:" + scenarioID);
    orchestrateEndpoint(camelRoute, outboundEndpointDefinition);
    camelRoute.to(outboundEndpointDefinition.getOutboundEndpoint());
  }

  private void buildRestEndpoint(
          RestEndpointDefinition restEndpointDefinition, String scenarioID) {

    RestDefinition restRoute = rest();
    bridgeEndpoint(restRoute, restEndpointDefinition);
    restRoute.to("direct:rest"+scenarioID);
    RouteDefinition camelRoute = from("direct:rest"+scenarioID);
    orchestrateEndpoint(camelRoute, restEndpointDefinition);
    camelRoute.to("sipmc:" + scenarioID);
  }

  private void bridgeEndpoint(RestDefinition restRoute, RestEndpointDefinition restEndpointDefinition) {
    RestEndpointBridgeInfo restEndpointBridgeInfo = () -> restRoute;
    restEndpointDefinition.getBridge().doBridge(restEndpointBridgeInfo);

  }

  private void orchestrateEndpoint(
      RouteDefinition camelRoute, Orchestratable<EndpointOrchestrationInfo> orchestratable) {

    EndpointOrchestrationInfo orchestrationInfo = () -> camelRoute;
    Orchestrator<EndpointOrchestrationInfo> orchestrator = orchestratable.getOrchestrator();
    if (orchestrator.canOrchestrate(orchestrationInfo)) {
      orchestrator.doOrchestrate(orchestrationInfo);
    }
  }
}
