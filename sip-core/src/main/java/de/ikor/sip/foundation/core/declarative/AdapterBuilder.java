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
import de.ikor.sip.foundation.core.declarative.validator.CDMValidator;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
      buildInboundEndpoint(definition, scenarioDefinition);
    }

    List<OutboundEndpointDefinition> outboundEndpointDefinitions =
        outboundEndpoints.get(scenarioDefinition);
    for (OutboundEndpointDefinition definition : outboundEndpointDefinitions) {
      buildOutboundEndpoint(definition, scenarioDefinition.getID());
    }
  }

  private void buildInboundEndpoint(
      InboundEndpointDefinition inboundEndpointDefinition,
      IntegrationScenarioDefinition scenarioDefinition) {
    String scenarioID = scenarioDefinition.getID();
    RouteDefinition camelRoute = createRouteDefinition(inboundEndpointDefinition, scenarioID);
    RestDefinition restDefinition = rest();
    EndpointOrchestrationInfo orchestrationInfo = createEndpointInfo(camelRoute, restDefinition);
    String routeId =
            String.format(
                    "in-%s-%s", inboundEndpointDefinition.getConnectorId(), scenarioDefinition.getID());
    camelRoute.routeId(routeId);
    orchestrateEndpoint(orchestrationInfo, inboundEndpointDefinition);
    bindRest(restDefinition, scenarioID);
    appendRequestValidation(scenarioDefinition.getRequestModelClass(), camelRoute);
    camelRoute.to(SIPMC + scenarioID);
    appendResponseValidation(scenarioDefinition.getResponseModelClass(), camelRoute);
  }

  private RouteDefinition createRouteDefinition(InboundEndpointDefinition inboundEndpointDefinition, String scenarioID) {
    return inboundEndpointDefinition instanceof RestEndpoint ? from(DIRECT_REST + scenarioID) : from(inboundEndpointDefinition.getInboundEndpoint());
  }

  private void bindRest(RestDefinition restDefinition, String scenarioID) {
    if (!restDefinition.getVerbs().isEmpty()) {
      restDefinition.to(DIRECT_REST + scenarioID);
    }
  }

  private EndpointOrchestrationInfo createEndpointInfo(
          RouteDefinition camelRoute, RestDefinition restDefinition) {
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

  private void buildOutboundEndpoint(
      OutboundEndpointDefinition outboundEndpointDefinition, String scenarioID) {

    RouteDefinition camelRoute = from(SIPMC + scenarioID);
    EndpointOrchestrationInfo orchestrationInfo = () -> camelRoute;
    orchestrateEndpoint(orchestrationInfo, outboundEndpointDefinition);
    camelRoute.to(outboundEndpointDefinition.getOutboundEndpoint());
    String routeId =
        String.format("out-%s-%s", outboundEndpointDefinition.getConnectorId(), scenarioID);
    camelRoute.routeId(routeId);
  }

  private void appendResponseValidation(
      Optional<Class<?>> responseModelClass, RouteDefinition camelRoute) {
    responseModelClass.ifPresent(aClass -> camelRoute.process(new CDMValidator(aClass)));
  }

  private void appendRequestValidation(Class<?> requestModelClass, RouteDefinition camelRoute) {
    camelRoute.process(new CDMValidator(requestModelClass));
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
