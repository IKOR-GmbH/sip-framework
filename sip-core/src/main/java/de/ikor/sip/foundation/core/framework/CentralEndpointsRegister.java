package de.ikor.sip.foundation.core.framework;

import lombok.Setter;
import org.apache.camel.Endpoint;

import java.util.HashMap;
import java.util.Map;

public class CentralEndpointsRegister {
  private static Map<String, Endpoint> registry = new HashMap<>();
  private static Map<String, InEndpoint> inEndpointRegistry = new HashMap<>();
  private static Map<String, InEndpoint> testingInEndpointRegistry = new HashMap<>();
  private static Map<String, Endpoint> testingOutEndpointRegistry = new HashMap<>();
  @Setter private static String state = "actual";

  private CentralEndpointsRegister() {}

  public static Endpoint getEndpoint(String endpointId) {
    if ("actual".equals(state)) {
      return registry.get(endpointId);
    }
    return testingOutEndpointRegistry.getOrDefault(endpointId, registry.get(endpointId));
  }

  public static InEndpoint getInEndpoint(String endpointId) {
    if ("actual".equals(state)) {
      return inEndpointRegistry.get(endpointId);
    }
    return testingInEndpointRegistry.get(endpointId);
  }

  public static void put(String endpointId, Endpoint endpoint) {
    registry.put(endpointId, endpoint);
    if (endpoint instanceof OutEndpoint) {
      testingOutEndpointRegistry.put(endpointId, toTestEndpoint((OutEndpoint) endpoint));
    }
  }

  public static void put(String id, InEndpoint inEndpoint) {
    inEndpointRegistry.put(id, inEndpoint);
    testingInEndpointRegistry.put(id, toTestEndpoint(inEndpoint));
  }

  private static InEndpoint toTestEndpoint(InEndpoint inEndpoint) {
    return new InEndpoint(modifyUriForTestRoute(inEndpoint.getUri()), inEndpoint.getId());
  }

  private static OutEndpoint toTestEndpoint(OutEndpoint outEndpoint) {
    return new OutEndpoint(modifyUriForTestRoute(outEndpoint.getEndpointUri()), outEndpoint.getEndpointId());
  }

  private static String modifyUriForTestRoute(String uri) {
    if(uri.startsWith("rest")){
      return uri.split(":")[0] + ":" + uri.split(":")[1] + ":" + uri.split(":")[2] + "-test";
    }
    return uri.split(":")[0] + ":" + uri.split(":")[1] + "-test";
  }

  public static String getInEndpointUri(String id) {
    return getInEndpoint(id).getUri();
  }
}
