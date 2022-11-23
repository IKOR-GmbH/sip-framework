package de.ikor.sip.foundation.core.framework.endpoints;

import org.apache.camel.Endpoint;

import java.util.HashMap;
import java.util.Map;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;

public class CentralEndpointsRegister {
  private static final Map<String, Endpoint> outEndpointRegistry = new HashMap<>();
  private static final Map<String, InEndpoint> inEndpointRegistry = new HashMap<>();

  private CentralEndpointsRegister() {}

  public static void put(String endpointId, OutEndpoint endpoint) {
    outEndpointRegistry.put(endpointId, endpoint);
  }

  public static void put(String id, InEndpoint inEndpoint) {
    inEndpointRegistry.put(id, inEndpoint);
  }

  public static Endpoint getCamelEndpoint(String uri) {
    return camelContext().getEndpoint(uri);
  }

  public static String getInEndpointUri(String id) {
    return getInEndpoint(id).getUri();
  }

  public static InEndpoint getInEndpoint(String endpointId) {
      return inEndpointRegistry.get(endpointId);
  }

  public static Endpoint getOutEndpoint(String endpointId) {
      return outEndpointRegistry.get(endpointId);
  }

}
