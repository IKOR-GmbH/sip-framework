package de.ikor.sip.foundation.core.framework;

import java.util.HashMap;
import java.util.Map;
import lombok.Setter;
import org.apache.camel.Endpoint;
import org.apache.commons.lang3.StringUtils;

public class CentralEndpointsRegister {

  private static final String COLON = ":";
  private static final String QUESTION = "?";
  private static final String TESTKIT_SUFFIX = "-testkit";

  private static Map<String, Endpoint> registry = new HashMap<>();
  private static Map<String, InEndpoint> inEndpointRegistry = new HashMap<>();
  private static Map<String, RestInEndpoint> restInEndpointRegistry = new HashMap<>();
  private static Map<String, InEndpoint> testingInEndpointRegistry = new HashMap<>();
  private static Map<String, RestInEndpoint> testingRestInEndpointRegistry = new HashMap<>();
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

  public static RestInEndpoint getRestInEndpoint(String endpointId) {
    if ("actual".equals(state)) {
      return restInEndpointRegistry.get(endpointId);
    }
    return testingRestInEndpointRegistry.get(endpointId);
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

  public static void put(String id, RestInEndpoint inEndpoint) {
    restInEndpointRegistry.put(id, inEndpoint);
    testingRestInEndpointRegistry.put(id, toTestEndpoint(inEndpoint));
  }

  private static InEndpoint toTestEndpoint(InEndpoint inEndpoint) {
    return new InEndpoint(modifyUriForTestRoute(inEndpoint.getUri()), inEndpoint.getId());
  }

  private static OutEndpoint toTestEndpoint(OutEndpoint outEndpoint) {
    return new OutEndpoint(
        modifyUriForTestRoute(outEndpoint.getEndpointUri()), outEndpoint.getEndpointId());
  }

  private static RestInEndpoint toTestEndpoint(RestInEndpoint restInEndpoint) {
    return new RestInEndpoint(
        restInEndpoint.getUri() + TESTKIT_SUFFIX,
        restInEndpoint.getId(),
        restInEndpoint.getRouteBuilder());
  }

  private static String modifyUriForTestRoute(String uri) {
    StringBuilder sb = new StringBuilder();
    String uriEndpoint = uri;
    String uriOptions = "";
    String componentName = uriEndpoint.split(COLON)[0];

    if (uri.contains(QUESTION)) {
      uriEndpoint = uri.split("\\?")[0];
      uriOptions = QUESTION + uri.split("\\?")[1];
    }

    sb.append(componentName);

    addMoreUriEndpointParts(sb, uriEndpoint);

    sb.append(TESTKIT_SUFFIX).append(uriOptions);
    return sb.toString();
  }

  private static void addMoreUriEndpointParts(StringBuilder sb, String uriEndpoint) {
    int numberOfColons = StringUtils.countMatches(uriEndpoint, COLON);
    for (int i = 1; i <= numberOfColons; i++) {
      sb.append(COLON).append(uriEndpoint.split(COLON)[i]);
    }
  }

  public static String getInEndpointUri(String id) {
    return getInEndpoint(id).getUri();
  }
}