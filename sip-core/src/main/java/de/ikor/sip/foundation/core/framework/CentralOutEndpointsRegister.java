package de.ikor.sip.foundation.core.framework;

import lombok.Setter;
import org.apache.camel.Endpoint;

import java.util.HashMap;
import java.util.Map;

public class CentralOutEndpointsRegister {
  private static Map<String, Endpoint> registry = new HashMap<>();
  private static Map<String, InEndpoint> inEndpointRegistry = new HashMap<>();
  private static Map<String, InEndpoint> testingInEndpointRegistry = new HashMap<>();
  @Setter private static String state = "actual";

  private CentralOutEndpointsRegister() {}

  public static Endpoint getEndpoint(String endpointId) {
    return registry.get(endpointId);
  }

  public static InEndpoint getInEndpoint(String endpointId) {
    if ("actual".equals(state)) {
      return inEndpointRegistry.get(endpointId);
    }
    return testingInEndpointRegistry.get(endpointId);
  }

  public static void put(String endpointId, Endpoint endpoint) {
    registry.put(endpointId, endpoint);
  }

  public static void put(String id, InEndpoint inEndpoint) {
    inEndpointRegistry.put(id, inEndpoint);
    testingInEndpointRegistry.put(id, toTestEndpoint(inEndpoint));
  }

  private static InEndpoint toTestEndpoint(InEndpoint inEndpoint) {
    return new InEndpoint(modifyUriForTestRoute(inEndpoint), inEndpoint.getId());
  }

  private static String modifyUriForTestRoute(InEndpoint inEndpoint) {
    return inEndpoint.getUri().split(":")[0] + ":" + inEndpoint.getUri().split(":")[1] + "-test";
  }

  public static String getInEndpointUri(String id) {
    return getInEndpoint(id).getUri();
  }
}
