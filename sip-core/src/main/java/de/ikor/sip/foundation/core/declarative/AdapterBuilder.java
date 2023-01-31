package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.endpoints.InboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.endpoints.OutboundEndpointDefinition;
import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestratable;
import de.ikor.sip.foundation.core.declarative.orchestation.Orchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.declarative.validator.CDMValidator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AdapterBuilder extends RouteBuilder {

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
    RouteDefinition camelRoute = from(inboundEndpointDefinition.getInboundEndpoint());
    orchestrateEndpoint(camelRoute, inboundEndpointDefinition);
    appendRequestValidation(scenarioDefinition.getRequestModelClass(), camelRoute);
    camelRoute.to("sipmc:" + scenarioDefinition.getID());
    appendResponseValidation(scenarioDefinition.getResponseModelClass(), camelRoute);
  }

  private void buildOutboundEndpoint(
      OutboundEndpointDefinition outboundEndpointDefinition, String scenarioID) {

    RouteDefinition camelRoute = from("sipmc:" + scenarioID);
    orchestrateEndpoint(camelRoute, outboundEndpointDefinition);
    camelRoute.to(outboundEndpointDefinition.getOutboundEndpoint());
  }

  private void appendResponseValidation(
      Optional<Class<?>> responseModelClass, RouteDefinition camelRoute) {
    responseModelClass.ifPresent(aClass -> camelRoute.process(new CDMValidator(aClass)));
  }

  private void appendRequestValidation(Class<?> requestModelClass, RouteDefinition camelRoute) {
    camelRoute.process(new CDMValidator(requestModelClass));
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
