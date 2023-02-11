package de.ikor.sip.foundation.core.declarative;

import de.ikor.sip.foundation.core.declarative.connector.ConnectorDefinition;
import de.ikor.sip.foundation.core.util.exception.SIPFrameworkInitializationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Synchronized;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.springframework.stereotype.Service;

@Service
public class RoutesRegistry {

  private final MultiValuedMap<ConnectorDefinition, String> routeIdsForConnectorRegister =
      new HashSetValuedHashMap<>();
  private final Map<String, ConnectorDefinition> connectorForRouteIdRegister = new HashMap<>();
  private final Map<String, RouteRole> roleForRouteIdRegister = new HashMap<>();

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
}
