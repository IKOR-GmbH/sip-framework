package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.connector.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioConsumerDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioDefinition;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioProviderDefinition;
import de.ikor.sip.foundation.core.declarative.validator.CDMValidator;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.builder.endpoint.dsl.DirectEndpointBuilderFactory;
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

  @SuppressWarnings("rawtypes")
  private final Map<IntegrationScenarioDefinition, List<InboundConnectorDefinition>>
      inboundConnectors;

  private final DeclarationsRegistry declarationsRegistry;
  private final RoutesRegistry routesRegistry;
  private final Map<IntegrationScenarioDefinition, List<OutboundConnectorDefinition>>
      outboundConnectors;

  @Override
  public void configure() {
    getCamelContext().getGlobalEndpointConfiguration().setBridgeErrorHandler(true);
    declarationsRegistry.getScenarios().forEach(this::buildScenario);
  }

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

  @SuppressWarnings("unchecked")
  private void buildScenario(final IntegrationScenarioDefinition scenarioDefinition) {
    final Map<
            IntegrationScenarioProviderDefinition,
            DirectEndpointBuilderFactory.DirectEndpointBuilder>
        providerHandoffEndpoints = new HashMap<>();
    final Map<
            IntegrationScenarioConsumerDefinition,
            DirectEndpointBuilderFactory.DirectEndpointBuilder>
        consumerHandonEndpoints = new HashMap<>();

    for (final var provider : inboundConnectors.get(scenarioDefinition)) {
      final var endpoint =
          StaticEndpointBuilders.direct(String.format("sip-scenario-handoff-%s", provider.getId()));
      providerHandoffEndpoints.put(provider, endpoint);
      buildInboundConnector(provider, scenarioDefinition, endpoint);
    }

    for (final var consumer : outboundConnectors.get(scenarioDefinition)) {
      final var endpoint =
          StaticEndpointBuilders.direct(String.format("sip-scenario-handon-%s", consumer.getId()));
      consumerHandonEndpoints.put(consumer, endpoint);
      buildOutboundConnector(consumer, scenarioDefinition, endpoint);
    }

    final var orchestrationInfo =
        new ScenarioOrchestrationValues(
            scenarioDefinition,
            getRouteCollection(),
            Collections.unmodifiableMap(providerHandoffEndpoints),
            Collections.unmodifiableMap(consumerHandonEndpoints));
    if (!scenarioDefinition.getOrchestrator().canOrchestrate(orchestrationInfo)) {
      throw new SIPFrameworkInitializationException(
          String.format(
              "Orchestrator assigned to scenario '%s' declares being unable to orchestrate the scenario layout as it is defined",
              scenarioDefinition.getId()));
    }
    scenarioDefinition.getOrchestrator().doOrchestrate(orchestrationInfo);
  }

  private <T extends OptionalIdentifiedDefinition<T>> void buildInboundConnector(
      final InboundConnectorDefinition<T> inboundConnector,
      final IntegrationScenarioDefinition scenarioDefinition,
      final EndpointProducerBuilder handoffToEndpoint) {

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
        StaticEndpointBuilders.direct(requestOrchestrationRouteId),
        routesRegistry);

    // Build scenario handoff and response-route
    final var handoffRouteDefinition =
        from(StaticEndpointBuilders.direct(scenarioHandoffRouteId))
            .routeId(scenarioHandoffRouteId)
            .process(
                new CDMValidator(
                    scenarioDefinition.getId(),
                    inboundConnector.getId(),
                    scenarioDefinition.getRequestModelClass()))
            .to(handoffToEndpoint);
    scenarioDefinition
        .getResponseModelClass()
        .ifPresent(
            model ->
                handoffRouteDefinition.process(
                    new CDMValidator(scenarioDefinition.getId(), inboundConnector.getId(), model)));

    if (inboundConnector.hasResponseFlow()) {
      handoffRouteDefinition.to(StaticEndpointBuilders.direct(responseOrchestrationRouteId));
    }

    // Build orchestration route(s) to/from scenario
    final var requestRouteDefinition =
        from(StaticEndpointBuilders.direct(requestOrchestrationRouteId))
            .routeId(requestOrchestrationRouteId);
    final Optional<RouteDefinition> responseRouteDefinition =
        inboundConnector.hasResponseFlow()
            ? Optional.of(
                from(StaticEndpointBuilders.direct(responseOrchestrationRouteId))
                    .routeId(responseOrchestrationRouteId))
            : Optional.empty();

    var orchestrationInfo =
        new OrchestrationRoutes(requestRouteDefinition, responseRouteDefinition);
    if (inboundConnector.getOrchestrator().canOrchestrate(orchestrationInfo)) {
      inboundConnector.getOrchestrator().doOrchestrate(orchestrationInfo);
    }
    requestRouteDefinition.to(StaticEndpointBuilders.direct(scenarioHandoffRouteId));
  }

  private void buildOutboundConnector(
      final OutboundConnectorDefinition outboundConnector,
      final IntegrationScenarioDefinition scenarioDefinition,
      final EndpointConsumerBuilder handonEndpoint) {

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
    from(handonEndpoint)
        .routeId(scenarioTakeoverRouteId)
        .to(StaticEndpointBuilders.direct(requestOrchestrationRouteId));

    // Build endpoint route that connects to external system
    final var endpointRouteDefinition =
        from(StaticEndpointBuilders.direct(externalEndpointRouteId))
            .routeId(externalEndpointRouteId);
    outboundConnector.defineOutboundEndpoints(endpointRouteDefinition);
    if (outboundConnector.hasResponseFlow()) {
      endpointRouteDefinition.to(StaticEndpointBuilders.direct(responseOrchestrationRouteId));
    }

    // Build orchestration route(s) to/from scenario
    final var requestRouteDefinition =
        from(StaticEndpointBuilders.direct(requestOrchestrationRouteId))
            .routeId(requestOrchestrationRouteId);
    final Optional<RouteDefinition> responseRouteDefinition =
        outboundConnector.hasResponseFlow()
            ? Optional.of(
                from(StaticEndpointBuilders.direct(responseOrchestrationRouteId))
                    .routeId(responseOrchestrationRouteId))
            : Optional.empty();

    var orchestrationInfo =
        new OrchestrationRoutes(requestRouteDefinition, responseRouteDefinition);
    if (outboundConnector.getOrchestrator().canOrchestrate(orchestrationInfo)) {
      outboundConnector.getOrchestrator().doOrchestrate(orchestrationInfo);
    }
    requestRouteDefinition.to(StaticEndpointBuilders.direct(externalEndpointRouteId));
  }

  @SuppressWarnings("unchecked")
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
    throw SIPFrameworkInitializationException.initException(
        "Failed to resolve unknown connector definition type: %s", type.getName());
  }

  @Value
  private static class OrchestrationRoutes implements ConnectorOrchestrationInfo {
    RouteDefinition requestRouteDefinition;
    Optional<RouteDefinition> responseRouteDefinition;
  }

  @Value
  private class ScenarioOrchestrationValues implements ScenarioOrchestrationInfo {
    IntegrationScenarioDefinition integrationScenario;
    RoutesDefinition routesDefinition;
    Map<IntegrationScenarioProviderDefinition, EndpointConsumerBuilder> providerEndpoints;
    Map<IntegrationScenarioConsumerDefinition, EndpointProducerBuilder> consumerEndpoints;
  }
}
