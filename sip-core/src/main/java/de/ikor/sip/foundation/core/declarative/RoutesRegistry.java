package de.ikor.sip.foundation.core.declarative;

import static de.ikor.sip.foundation.core.proxies.ProcessorProxy.NON_OUTGOING_PROCESSOR_PREFIXES;

import de.ikor.sip.foundation.core.actuator.declarative.model.EndpointInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.RouteDeclarativeStructureInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.RouteInfo;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.core.proxies.ProcessorProxyRegistry;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Synchronized;
import org.apache.camel.*;
import org.apache.camel.component.servlet.ServletConsumer;
import org.apache.camel.processor.*;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.CamelEvent.CamelContextEvent;
import org.apache.camel.spi.CamelEvent.CamelContextStartedEvent;
import org.apache.camel.spi.IdAware;
import org.apache.camel.support.SimpleEventNotifierSupport;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class RoutesRegistry extends SimpleEventNotifierSupport {

  public static final String SIP_CONNECTOR_PREFIX = "sip-connector";
  public static final String SIP_SOAP_SERVICE_PREFIX = "sip-soap-service";
  private final DeclarationsRegistryApi declarationsRegistryApi;

  private final MultiValuedMap<ConnectorDefinition, String> routeIdsForConnectorRegister =
      new HashSetValuedHashMap<>();
  private final Map<String, ConnectorDefinition> connectorForRouteIdRegister = new HashMap<>();

  private final Map<String, String> routeIdForSoapServiceRegister = new HashMap<>();
  private final Map<String, RouteRole> roleForRouteIdRegister = new HashMap<>();
  private final MultiValuedMap<String, String> endpointsForRouteId = new HashSetValuedHashMap<>();
  private final MultiValuedMap<String, String> routeIdsForEndpoints = new HashSetValuedHashMap<>();

  private final Map<String, IdAware> outgoingEndpointIds = new HashMap<>();

  public RoutesRegistry(DeclarationsRegistryApi declarationsRegistryApi) {
    this.declarationsRegistryApi = declarationsRegistryApi;
  }

  /** On CamelContextStartedEvent execute this class's event listener - notify() */
  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof CamelContextStartedEvent;
  }

  /** Trigger caching of routes and endpoints mappings */
  @Override
  public void notify(CamelEvent event) {
    prefillEndpointRouteMappings(((CamelContextEvent) event).getContext());
  }

  @Synchronized
  public String generateRouteIdForConnector(
      final RouteRole role, final ConnectorDefinition connector, final Object... suffixes) {
    final var idBuilder =
        new StringBuilder(
            String.format(
                "%s_%s_%s",
                SIP_CONNECTOR_PREFIX, connector.getId(), role.getRoleSuffixInRouteId()));
    Arrays.stream(suffixes).forEach(suffix -> idBuilder.append("-").append(suffix));
    final var routeId = idBuilder.toString();
    if (roleForRouteIdRegister.containsKey(routeId)) {
      throw new SIPFrameworkInitializationException(
          String.format(
              "Can't build internal connector route with routeId '%s': routeId already exists",
              routeId));
    }
    connectorForRouteIdRegister.put(routeId, connector);
    routeIdsForConnectorRegister.put(connector, routeId);
    roleForRouteIdRegister.put(routeId, role);
    return routeId;
  }

  @Synchronized
  public String generateRouteIdForSoapService(final String soapServiceName) {
    final var routeId = String.format("%s_%s", SIP_SOAP_SERVICE_PREFIX, soapServiceName);
    if (roleForRouteIdRegister.containsKey(routeId)) {
      throw new SIPFrameworkInitializationException(
          String.format(
              "Can't build internal soap-service route with routeId '%s': routeId already exists",
              routeId));
    }
    routeIdForSoapServiceRegister.put(routeId, soapServiceName);
    roleForRouteIdRegister.put(routeId, RouteRole.EXTERNAL_SOAP_SERVICE_PROXY);
    return routeId;
  }

  public RouteDeclarativeStructureInfo generateRouteInfo(String routeId) {
    ConnectorDefinition connectorDefinition = connectorForRouteIdRegister.get(routeId);
    if (connectorDefinition == null) {
      return null;
    }
    return RouteDeclarativeStructureInfo.builder()
        .connectorGroupId(connectorDefinition.getConnectorGroupId())
        .connectorId(connectorDefinition.getId())
        .scenarioId(connectorDefinition.getScenarioId())
        .build();
  }

  public String getRouteIdByConnectorId(String connectorId) {
    Optional<ConnectorDefinition> connector = declarationsRegistryApi.getConnectorById(connectorId);
    List<RouteInfo> routeInfos = new ArrayList<>();
    if (connector.isPresent()) {
      routeInfos = getRoutesInfo(connector.get());
    }
    return routeInfos.stream()
        .filter(
            routeInfo ->
                routeInfo.getRouteRole().equals(RouteRole.EXTERNAL_ENDPOINT.getExternalName()))
        .map(RouteInfo::getRouteId)
        .findFirst()
        .orElse(null);
  }

  public List<RouteInfo> getRoutesInfo(ConnectorDefinition connectorDefinition) {
    return routeIdsForConnectorRegister.get(connectorDefinition).stream()
        .map(
            routeId ->
                RouteInfo.builder()
                    .routeRole(roleForRouteIdRegister.get(routeId).getExternalName())
                    .routeId(routeId)
                    .build())
        .toList();
  }

  public List<EndpointInfo> getExternalEndpointInfosForConnector(
      ConnectorDefinition connectorDefinition) {
    List<RouteInfo> connectorRoutes = getRoutesInfo(connectorDefinition);
    return connectorRoutes.stream()
        .map(
            routeInfo ->
                endpointsForRouteId.get(routeInfo.getRouteId()).stream()
                    // filter out all of sip framework internal endpoints
                    .filter(endpoint -> !endpoint.contains(SIP_CONNECTOR_PREFIX))
                    .filter(
                        endpoint ->
                            !StringUtils.startsWithAny(endpoint, NON_OUTGOING_PROCESSOR_PREFIXES))
                    .map(endpoint -> createEndpointInfo(endpoint, routeInfo.getRouteId()))
                    .toList())
        .flatMap(Collection::stream)
        .toList();
  }

  private EndpointInfo createEndpointInfo(String endpoint, String routeId) {
    IdAware idAware = outgoingEndpointIds.get(endpoint);
    return EndpointInfo.builder()
        .endpointId(idAware != null ? idAware.getId() : routeId)
        .camelEndpointUri(endpoint)
        .build();
  }

  public List<RouteDeclarativeStructureInfo> generateRouteInfoList(Endpoint endpoint) {
    List<RouteDeclarativeStructureInfo> routeDeclarativeStructureInfoList = new ArrayList<>();
    routeIdsForEndpoints
        .get(endpoint.getEndpointBaseUri())
        .forEach(routeId -> routeDeclarativeStructureInfoList.add(generateRouteInfo(routeId)));
    return routeDeclarativeStructureInfoList;
  }

  void prefillEndpointRouteMappings(CamelContext camelContext) {
    initOutgoingEndpointIds(camelContext);
    initEndpointsAndRouteIdsMaps(camelContext);
  }

  private void initOutgoingEndpointIds(CamelContext camelContext) {
    ProcessorProxyRegistry proxiesRegistry =
        camelContext.getExtension(ProcessorProxyRegistry.class);
    proxiesRegistry
        .getProxies()
        .forEach((processorId, processor) -> checkAndAddProcessorId(processor));
  }

  private void checkAndAddProcessorId(ProcessorProxy processor) {
    if (processor.isEndpointProcessor()) {
      Processor originalProcessor = processor.getOriginalProcessor();
      if (originalProcessor instanceof SendProcessor sendProcessor) {
        outgoingEndpointIds.put(sendProcessor.getEndpoint().getEndpointBaseUri(), sendProcessor);
      }
      if (originalProcessor instanceof SendDynamicProcessor dynamicProcessor) {
        outgoingEndpointIds.put(dynamicProcessor.getUri(), dynamicProcessor);
      }
      if (originalProcessor instanceof WireTapProcessor wireTapProcessor) {
        outgoingEndpointIds.put(wireTapProcessor.getUri(), wireTapProcessor);
      }
    }
  }

  private void initEndpointsAndRouteIdsMaps(CamelContext camelContext) {
    int enrichCounter = 1;
    int pollEnrichCounter = 1;
    for (Route route : camelContext.getRoutes()) {
      String routeId = route.getRouteId();
      addToEndpointUriMaps(routeId, route.getEndpoint().getEndpointBaseUri());
      for (org.apache.camel.Service service : route.getServices()) {
        if (service instanceof ServletConsumer) {
          continue;
        }
        if (service instanceof EndpointAware endpointAware) {
          addToEndpointUriMaps(routeId, endpointAware.getEndpoint().getEndpointBaseUri());
        }
        if (service instanceof SendDynamicProcessor dynamicProcessor) {
          addToEndpointUriMaps(routeId, dynamicProcessor.getUri());
        }
        if (service instanceof WireTapProcessor wireTapProcessor) {
          addToEndpointUriMaps(routeId, wireTapProcessor.getUri());
        }
        if (service instanceof Enricher enricher) {
          String enrichEndpointUri =
              enricher.getExpression() != null
                  ? enricher.getExpression().toString()
                  : String.format("enrich-%s", enrichCounter++);
          addToEndpointUriMaps(routeId, enrichEndpointUri);
          outgoingEndpointIds.put(enrichEndpointUri, enricher);
        }
        if (service instanceof PollEnricher pollEnricher) {
          String pollEnrichEndpointUri =
              pollEnricher.getExpression() != null
                  ? pollEnricher.getExpression().toString()
                  : String.format("pollEnrich-%s", pollEnrichCounter++);
          addToEndpointUriMaps(routeId, pollEnrichEndpointUri);
          outgoingEndpointIds.put(pollEnrichEndpointUri, pollEnricher);
        }
      }
    }
  }

  private void addToEndpointUriMaps(String routeId, String endpointUri) {
    endpointsForRouteId.put(routeId, endpointUri);
    routeIdsForEndpoints.put(endpointUri, routeId);
  }
}
