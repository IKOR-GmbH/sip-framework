package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.actuator.declarative.model.RouteDeclarativeStructureInfo;
import de.ikor.sip.foundation.core.actuator.declarative.model.RouteInfo;
import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Synchronized;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointAware;
import org.apache.camel.Route;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.CamelEvent.CamelContextEvent;
import org.apache.camel.spi.CamelEvent.CamelContextStartedEvent;
import org.apache.camel.support.SimpleEventNotifierSupport;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
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
  private final MultiValuedMap<String, Endpoint> endpointsForRouteId = new HashSetValuedHashMap<>();
  private final MultiValuedMap<Endpoint, String> routeIdsForEndpoints =
      new HashSetValuedHashMap<>();

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

  public String getRouteIdByConnectorIdAndRole(String connectorId, RouteRole role) {
    Optional<ConnectorDefinition> connector = declarationsRegistryApi.getConnectorById(connectorId);
    List<RouteInfo> routeInfos = new ArrayList<>();
    if (connector.isPresent()) {
      routeInfos = getRoutesInfo(connector.get());
    }
    return routeInfos.stream()
        .filter(routeInfo -> routeInfo.getRouteRole().equals(role.getExternalName()))
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

  public ConnectorDefinition getConnectorByRouteId(String routeId) {
    return connectorForRouteIdRegister.get(routeId);
  }

  public List<Endpoint> getExternalEndpointsForConnector(ConnectorDefinition connectorDefinition) {
    List<RouteInfo> connectorRoutes = getRoutesInfo(connectorDefinition);
    return connectorRoutes.stream()
        .filter(
            routeInfo ->
                routeInfo.getRouteRole().equals(RouteRole.EXTERNAL_ENDPOINT.getExternalName()))
        .map(
            routeInfo ->
                endpointsForRouteId.get(routeInfo.getRouteId()).stream()
                    // filter out all of sip framework internal endpoints
                    .filter(endpoint -> !endpoint.getEndpointKey().contains(SIP_CONNECTOR_PREFIX))
                    .toList())
        .flatMap(Collection::stream)
        .toList();
  }

  public List<RouteDeclarativeStructureInfo> generateRouteInfoList(Endpoint endpoint) {
    List<RouteDeclarativeStructureInfo> routeDeclarativeStructureInfoList = new ArrayList<>();
    routeIdsForEndpoints
        .get(endpoint)
        .forEach(routeId -> routeDeclarativeStructureInfoList.add(generateRouteInfo(routeId)));
    return routeDeclarativeStructureInfoList;
  }

  void prefillEndpointRouteMappings(CamelContext camelContext) {
    for (Route route : camelContext.getRoutes()) {
      String routeId = route.getRouteId();
      endpointsForRouteId.put(routeId, route.getEndpoint());
      routeIdsForEndpoints.put(route.getEndpoint(), routeId);
      for (org.apache.camel.Service service : route.getServices()) {
        if (service instanceof EndpointAware endpointAware) {
          Endpoint endpoint = endpointAware.getEndpoint();
          endpointsForRouteId.put(routeId, endpoint);
          routeIdsForEndpoints.put(endpoint, routeId);
        }
      }
    }
  }
}
