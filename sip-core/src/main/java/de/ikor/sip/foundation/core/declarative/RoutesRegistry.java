package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.actuator.declarative.model.RouteDeclarativeStructureInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.RouteInfo;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Synchronized;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointAware;
import org.apache.camel.Route;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class RoutesRegistry {

  private final CamelContext camelContext;
  private final DeclarationRegistryApi declarationRegistryApi;

  private final MultiValuedMap<ConnectorDefinition, String> routeIdsForConnectorRegister =
      new HashSetValuedHashMap<>();
  private final Map<String, ConnectorDefinition> connectorForRouteIdRegister = new HashMap<>();
  private final Map<String, RouteRole> roleForRouteIdRegister = new HashMap<>();

  private final MultiValuedMap<String, Endpoint> endpointsForRouteId = new HashSetValuedHashMap<>();
  private final MultiValuedMap<Endpoint, String> routeIdsForEndpoints =
      new HashSetValuedHashMap<>();

  public RoutesRegistry(DeclarationRegistryApi declarationRegistryApi, CamelContext camelContext) {
    this.declarationRegistryApi = declarationRegistryApi;
    this.camelContext = camelContext;
  }

  /** On ApplicationReadyEvent trigger mapping routes and endpoints */
  @EventListener(ApplicationReadyEvent.class)
  private void fillRegistry() {
    prefillEndpointRouteMappings();
  }

  @Synchronized
  public String generateRouteIdForConnector(
      final RouteRole role, final ConnectorDefinition connector, final Object... suffixes) {
    final var idBuilder =
        new StringBuilder(
            String.format("sip-connector_%s_%s", connector.getId(), role.getRoleSuffixInRouteId()));
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
    Optional<ConnectorDefinition> connector = declarationRegistryApi.getConnectorById(connectorId);
    List<RouteInfo> routeInfos = new ArrayList<>();
    if (connector.isPresent()) {
      routeInfos = getRoutesInfo(connector.get());
    }
    return routeInfos.stream()
        .filter(routeInfo -> routeInfo.getRouteRole().equals(RouteRole.EXTERNAL_ENDPOINT))
        .map(RouteInfo::getRouteId)
        .findFirst()
        .orElse(null);
  }

  public List<RouteInfo> getRoutesInfo(ConnectorDefinition connectorDefinition) {
    return routeIdsForConnectorRegister.get(connectorDefinition).stream()
        .map(
            routeId ->
                RouteInfo.builder()
                    .routeRole(roleForRouteIdRegister.get(routeId))
                    .routeId(routeId)
                    .build())
        .collect(Collectors.toList());
  }

  public List<RouteDeclarativeStructureInfo> generateRouteInfoList(Endpoint endpoint) {
    List<RouteDeclarativeStructureInfo> routeDeclarativeStructureInfoList = new ArrayList<>();
    routeIdsForEndpoints
        .get(endpoint)
        .forEach(routeId -> routeDeclarativeStructureInfoList.add(generateRouteInfo(routeId)));
    return routeDeclarativeStructureInfoList;
  }

  private void prefillEndpointRouteMappings() {
    for (Route route : camelContext.getRoutes()) {
      String routeId = route.getRouteId();
      endpointsForRouteId.put(routeId, route.getEndpoint());
      routeIdsForEndpoints.put(route.getEndpoint(), routeId);
      for (org.apache.camel.Service service : route.getServices()) {
        if (service instanceof EndpointAware) {
          Endpoint endpoint = ((EndpointAware) service).getEndpoint();
          endpointsForRouteId.put(routeId, endpoint);
          routeIdsForEndpoints.put(endpoint, routeId);
        }
      }
    }
  }
}
