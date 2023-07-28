package de.ikor.sip.foundation.core.declarative;

import static de.ikor.sip.foundation.core.declarative.validator.CDMValidator.*;

import de.ikor.sip.foundation.core.declarative.connector.InboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.connector.OutboundConnectorDefinition;
import de.ikor.sip.foundation.core.declarative.orchestration.connector.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.process.CompositeOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.process.CompositeProcessDefinition;
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

  private String COMPOSITE_HANDOFF_ROUTE_ID_PATTERN = "sip-composite-handoff-%s";
  private String COMPOSITE_TAKOVER_ROUTE_ID_PATTERN = "sip-composite-takeover-%s";
  private final DeclarationsRegistry declarationsRegistry;
  private final RoutesRegistry routesRegistry;

  @SuppressWarnings("rawtypes")
  private final Map<IntegrationScenarioDefinition, List<InboundConnectorDefinition>>
      inboundConnectors;

  private final Map<IntegrationScenarioDefinition, List<OutboundConnectorDefinition>>
      outboundConnectors;

  @Override
  public void configure() {
    getCamelContext().getGlobalEndpointConfiguration().setBridgeErrorHandler(true);
    declarationsRegistry.getScenarios().forEach(this::buildScenario);
    declarationsRegistry.getProcesses().forEach(this::buildCompositeProcess);
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

    if (inboundConnectors.get(scenarioDefinition) != null) {
      for (final var provider : inboundConnectors.get(scenarioDefinition)) {
        final var endpoint =
            StaticEndpointBuilders.direct(
                String.format("sip-scenario-handoff-%s", provider.getId()));
        providerHandoffEndpoints.put(provider, endpoint);
        buildInboundConnector(provider, scenarioDefinition, endpoint);
      }
    }

    declarationsRegistry
        .getCompositeProcessProvidersForScenario(scenarioDefinition)
        .forEach(
            composite -> {
              final var endpoint =
                  StaticEndpointBuilders.direct(
                      String.format(
                          COMPOSITE_HANDOFF_ROUTE_ID_PATTERN,
                          composite.getId() + "-" + scenarioDefinition.getId()));
              providerHandoffEndpoints.put(composite, endpoint);
            });

    final Map<
            IntegrationScenarioConsumerDefinition,
            DirectEndpointBuilderFactory.DirectEndpointBuilder>
        consumerTakeoverEndpoints = new HashMap<>();

    if (outboundConnectors.get(scenarioDefinition) != null) {
      for (final var consumer : outboundConnectors.get(scenarioDefinition)) {
        final var endpoint =
            StaticEndpointBuilders.direct(
                String.format("sip-scenario-takeover-%s", consumer.getId()));
        consumerTakeoverEndpoints.put(consumer, endpoint);
        buildOutboundConnector(consumer, scenarioDefinition, endpoint);
      }
    }

    declarationsRegistry
        .getCompositeProcessConsumersForScenario(scenarioDefinition)
        .forEach(
            composite -> {
              final var endpoint =
                  StaticEndpointBuilders.direct(
                      String.format(
                          COMPOSITE_TAKOVER_ROUTE_ID_PATTERN,
                          composite.getId() + "-" + scenarioDefinition.getId()));
              consumerTakeoverEndpoints.put(composite, endpoint);
            });

    final var orchestrationInfo =
        new ScenarioOrchestrationValues(
            scenarioDefinition,
            getRouteCollection(),
            Collections.unmodifiableMap(providerHandoffEndpoints),
            Collections.unmodifiableMap(consumerTakeoverEndpoints));
    if (!scenarioDefinition.getOrchestrator().canOrchestrate(orchestrationInfo)) {
      throw SIPFrameworkInitializationException.init(
          "Orchestrator assigned to scenario '%s' declares being unable to orchestrate the scenario layout as it is defined",
          scenarioDefinition.getId());
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
                    scenarioDefinition.getRequestModelClass(),
                    TO_CDM_EXCEPTION_MESSAGE))
            .to(handoffToEndpoint);

    if (inboundConnector.hasResponseFlow()) {
      scenarioDefinition
          .getResponseModelClass()
          .ifPresent(
              cdmModel ->
                  handoffRouteDefinition.process(
                      new CDMValidator(
                          scenarioDefinition.getId(),
                          inboundConnector.getId(),
                          cdmModel,
                          FROM_CDM_EXCEPTION_MESSAGE)));
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
      final EndpointConsumerBuilder takeoverFromEndpoint) {

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
    from(takeoverFromEndpoint)
        .routeId(scenarioTakeoverRouteId)
        .process(
            new CDMValidator(
                scenarioDefinition.getId(),
                outboundConnector.getId(),
                scenarioDefinition.getRequestModelClass(),
                FROM_CDM_EXCEPTION_MESSAGE))
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

    scenarioDefinition
        .getResponseModelClass()
        .ifPresent(
            cdmModel ->
                responseRouteDefinition.ifPresent(
                    route ->
                        route.process(
                            new CDMValidator(
                                scenarioDefinition.getId(),
                                outboundConnector.getId(),
                                cdmModel,
                                TO_CDM_EXCEPTION_MESSAGE))));
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
    throw SIPFrameworkInitializationException.init(
        "Failed to resolve unknown connector definition type: %s", type.getName());
  }

  private void buildCompositeProcess(CompositeProcessDefinition compositeProcess) {

    final Map<IntegrationScenarioDefinition, DirectEndpointBuilderFactory.DirectEndpointBuilder>
        providerHandoffEndpoints = new HashMap<>();
    final Map<IntegrationScenarioDefinition, DirectEndpointBuilderFactory.DirectEndpointBuilder>
        consumerTakeoverEndpoints = new HashMap<>();

    compositeProcess
        .getProviderDefinitions()
        .forEach(
            provider -> {
              IntegrationScenarioDefinition providerScenario =
                  declarationsRegistry.getScenarios().stream()
                      .filter(s -> s.getClass().equals(provider))
                      .findFirst()
                      .get();
              final var startingEndpoint =
                  StaticEndpointBuilders.direct(
                      String.format(
                          COMPOSITE_TAKOVER_ROUTE_ID_PATTERN,
                          compositeProcess.getId() + "-" + providerScenario.getId()));
              providerHandoffEndpoints.put(providerScenario, startingEndpoint);
            });
    compositeProcess
        .getConsumerDefinitions()
        .forEach(
            consumer -> {
              IntegrationScenarioDefinition consumerScenario =
                  declarationsRegistry.getScenarios().stream()
                      .filter(s -> s.getClass().equals(consumer))
                      .findFirst()
                      .get();
              var endingEndpoint =
                  StaticEndpointBuilders.direct(
                      String.format(
                          COMPOSITE_HANDOFF_ROUTE_ID_PATTERN,
                          compositeProcess.getId() + "-" + consumerScenario.getId()));
              consumerTakeoverEndpoints.put(consumerScenario, endingEndpoint);
            });

    final var orchestrationInfo =
        new CompositeOrchestrationValues(
            compositeProcess,
            getRouteCollection(),
            Collections.unmodifiableMap(providerHandoffEndpoints),
            Collections.unmodifiableMap(consumerTakeoverEndpoints));
    if (!compositeProcess.getOrchestrator().canOrchestrate(orchestrationInfo)) {
      throw SIPFrameworkInitializationException.init(
          "Orchestrator assigned to composite process '%s' declares being unable to orchestrate the orchestration layout as it is defined",
          compositeProcess.getId());
    }
    compositeProcess.getOrchestrator().doOrchestrate(orchestrationInfo);
  }

  @Value
  private static class OrchestrationRoutes implements ConnectorOrchestrationInfo {

    RouteDefinition requestRouteDefinition;
    Optional<RouteDefinition> responseRouteDefinition;
  }

  @Value
  private static class ScenarioOrchestrationValues implements ScenarioOrchestrationInfo {

    IntegrationScenarioDefinition integrationScenario;
    RoutesDefinition routesDefinition;
    Map<IntegrationScenarioProviderDefinition, EndpointConsumerBuilder> providerEndpoints;
    Map<IntegrationScenarioConsumerDefinition, EndpointProducerBuilder> consumerEndpoints;
  }

  @Value
  private static class CompositeOrchestrationValues implements CompositeOrchestrationInfo {

    CompositeProcessDefinition compositeProcess;
    RoutesDefinition routesDefinition;
    Map<IntegrationScenarioDefinition, EndpointConsumerBuilder> providerEndpoints;
    Map<IntegrationScenarioDefinition, EndpointProducerBuilder> consumerEndpoints;
  }
}
