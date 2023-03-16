package de.ikor.sip.foundation.core.declarative;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;

import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.validator.CDMValidator;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.model.rest.RestsDefinition;
import org.springframework.stereotype.Component;

/**
 * This class is setting up Camel routes from the scenario- and connector-definitions specified in
 * the adapter.
 *
 * <p><em>Intended for internal use only</em>
 */
@Component
@Slf4j
public class AdapterBuilder extends RouteBuilder {

  private final DeclarationsRegistry declarationsRegistry;
  private final RoutesRegistry routesRegistry;

  @SuppressWarnings("rawtypes")
  private final Map<IntegrationScenarioDefinition, List<InboundConnectorDefinition>>
      inboundConnectors;

  private final Map<IntegrationScenarioDefinition, List<OutboundConnectorDefinition>>
      outboundConnectors;

  public AdapterBuilder(DeclarationsRegistry declarationsRegistry, RoutesRegistry routesRegistry) {
    this.declarationsRegistry = declarationsRegistry;
    this.routesRegistry = routesRegistry;
    this.inboundConnectors =
        declarationsRegistry.getInboundConnectors().stream()
            .collect(
                Collectors.groupingBy(
                    connectors -> declarationsRegistry.getScenarioById(connectors.toScenarioId())));
    this.outboundConnectors =
        declarationsRegistry.getOutboundConnectors().stream()
            .collect(
                Collectors.groupingBy(
                    connectors ->
                        declarationsRegistry.getScenarioById(connectors.fromScenarioId())));
  }

  @Override
  public void configure() {
    getCamelContext().getGlobalEndpointConfiguration().setBridgeErrorHandler(true);
    declarationsRegistry.getScenarios().forEach(this::buildScenario);
  }

  private void buildScenario(final IntegrationScenarioDefinition scenarioDefinition) {
    List<InboundConnectorDefinition> scenariosInboundConnectors =
        inboundConnectors.get(scenarioDefinition);
    List<OutboundConnectorDefinition> scenariosOutboundConnectors =
        outboundConnectors.get(scenarioDefinition);

    scenariosInboundConnectors.forEach(
        connectorDefinition -> buildInboundConnector(connectorDefinition, scenarioDefinition));

    scenariosOutboundConnectors.forEach(
        connectorDefinition -> buildOutboundConnector(connectorDefinition, scenarioDefinition));

    ScenarioOrchestrationInfo scenarioOrchestrationInfo =
        new ScenarioOrchestrationRoutes(
            scenariosInboundConnectors.stream()
                .map(connector -> from(direct(connector.getId())))
                .toList(),
            scenariosOutboundConnectors.stream().map(c -> direct(c.getId())).toList());
    if (scenarioDefinition.getOrchestrator().canOrchestrate(scenarioOrchestrationInfo)) {
      scenarioDefinition.getOrchestrator().doOrchestrate(scenarioOrchestrationInfo);
    }
  }

  private <T extends OptionalIdentifiedDefinition<T>> void buildInboundConnector(
      final InboundConnectorDefinition<T> inboundConnector,
      final IntegrationScenarioDefinition scenarioDefinition) {

    final var requestOrchestrationRouteId =
        routesRegistry.generateRouteIdForConnector(
            RouteRole.CONNECTOR_REQUEST_ORCHESTRATION, inboundConnector);
    final var responseOrchestrationRouteId =
        routesRegistry.generateRouteIdForConnector(
            RouteRole.CONNECTOR_RESPONSE_ORCHESTRATION, inboundConnector);
    final var scenarioHandoffRouteId =
        routesRegistry.generateRouteIdForConnector(RouteRole.SCENARIO_HANDOFF, inboundConnector);

    // Channel all inbound camel endpoint routes to the orchestration route
    final var endpointDefinitionType = inboundConnector.getEndpointDefinitionTypeClass();
    inboundConnector.defineInboundEndpoints(
        resolveConnectorDefinitionType(endpointDefinitionType),
        direct(requestOrchestrationRouteId),
        routesRegistry);

    // Build scenario handoff and response-route
    final var handoffRouteDefinition =
        from(direct(scenarioHandoffRouteId))
            .routeId(scenarioHandoffRouteId)
            .process(
                new CDMValidator(
                    scenarioDefinition
                        .getRequestModelClass())) // TODO: move validation to camel-component
            .to(direct(inboundConnector.getId()));
    scenarioDefinition
        .getResponseModelClass()
        .ifPresent(
            model ->
                handoffRouteDefinition.process(
                    new CDMValidator(model))); // TODO: move validation to camel-component

    if (inboundConnector.hasResponseFlow()) {
      handoffRouteDefinition.to(direct(responseOrchestrationRouteId));
    }

    // Build orchestration route(s) to/from scenario
    final var requestRouteDefinition =
        from(direct(requestOrchestrationRouteId)).routeId(requestOrchestrationRouteId);
    final Optional<RouteDefinition> responseRouteDefinition =
        inboundConnector.hasResponseFlow()
            ? Optional.of(
                from(direct(responseOrchestrationRouteId)).routeId(responseOrchestrationRouteId))
            : Optional.empty();

    var orchestrationInfo =
        new OrchestrationRoutes(requestRouteDefinition, responseRouteDefinition);
    if (inboundConnector.getOrchestrator().canOrchestrate(orchestrationInfo)) {
      inboundConnector.getOrchestrator().doOrchestrate(orchestrationInfo);
    }
    requestRouteDefinition.to(direct(scenarioHandoffRouteId));
  }

  private void buildOutboundConnector(
      final OutboundConnectorDefinition outboundConnector,
      final IntegrationScenarioDefinition scenarioDefinition) {

    final var externalEndpointRouteId =
        routesRegistry.generateRouteIdForConnector(RouteRole.EXTERNAL_ENDPOINT, outboundConnector);
    final var requestOrchestrationRouteId =
        routesRegistry.generateRouteIdForConnector(
            RouteRole.CONNECTOR_REQUEST_ORCHESTRATION, outboundConnector);
    final var responseOrchestrationRouteId =
        routesRegistry.generateRouteIdForConnector(
            RouteRole.CONNECTOR_RESPONSE_ORCHESTRATION, outboundConnector);
    final var scenarioTakeoverRouteId =
        routesRegistry.generateRouteIdForConnector(RouteRole.SCENARIO_TAKEOVER, outboundConnector);

    // Build takeover route from scenario
    from(direct(outboundConnector.getId()))
        .routeId(scenarioTakeoverRouteId)
        .to(direct(requestOrchestrationRouteId));

    // Build endpoint route that connects to external system
    final var endpointRouteDefinition =
        from(direct(externalEndpointRouteId)).routeId(externalEndpointRouteId);
    outboundConnector.defineOutboundEndpoints(endpointRouteDefinition);
    if (outboundConnector.hasResponseFlow()) {
      endpointRouteDefinition.to(direct(responseOrchestrationRouteId));
    }

    // Build orchestration route(s) to/from scenario
    final var requestRouteDefinition =
        from(direct(requestOrchestrationRouteId)).routeId(requestOrchestrationRouteId);
    final Optional<RouteDefinition> responseRouteDefinition =
        outboundConnector.hasResponseFlow()
            ? Optional.of(
                from(direct(responseOrchestrationRouteId)).routeId(responseOrchestrationRouteId))
            : Optional.empty();

    endpointRouteDefinition.setProperty("sip-connector-id").constant(outboundConnector.getId());
    var orchestrationInfo =
        new OrchestrationRoutes(requestRouteDefinition, responseRouteDefinition);
    if (outboundConnector.getOrchestrator().canOrchestrate(orchestrationInfo)) {
      outboundConnector.getOrchestrator().doOrchestrate(orchestrationInfo);
    }
    requestRouteDefinition.to(direct(externalEndpointRouteId));
  }

  private <T extends OptionalIdentifiedDefinition<T>> T resolveConnectorDefinitionType(
      Class<? extends T> type) {
    if (type.equals(RoutesDefinition.class)) {
      var routeCollection = getRouteCollection();
      routeCollection.setCamelContext(getCamelContext());
      return (T) routeCollection;
    }
    if (type.equals(RestsDefinition.class)) {
      var restCollection = getRestCollection();
      restCollection.setCamelContext(getCamelContext());
      return (T) restCollection;
    }
    throw new SIPFrameworkInitializationException(
        String.format("Failed to resolve unknown connector definition type: %s", type.getName()));
  }

  private String sipMC(String scenarioId) {
    return String.format("sipmc:%s", scenarioId);
  }

  @Value
  private static class OrchestrationRoutes implements ConnectorOrchestrationInfo {
    RouteDefinition requestRouteDefinition;
    Optional<RouteDefinition> responseRouteDefinition;
  }

  @Value
  private static class ScenarioOrchestrationRoutes implements ScenarioOrchestrationInfo {

    List<RouteDefinition> inboundConnectorRouteEnds;
    List<? extends EndpointProducerBuilder> outboundConnectorsStarts;
  }
}
