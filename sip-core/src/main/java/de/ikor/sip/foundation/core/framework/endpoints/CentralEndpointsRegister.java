package de.ikor.sip.foundation.core.framework.endpoints;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static de.ikor.sip.foundation.core.framework.util.TestingRoutesUtil.TESTING_SUFFIX;

import java.util.HashMap;
import java.util.Map;
import org.apache.camel.Endpoint;
import org.apache.commons.lang3.StringUtils;

public class CentralEndpointsRegister {
  private static final String COLON = ":";
  private static final String QUESTION = "?";

  private static final Map<String, Endpoint> outEndpointRegistry = new HashMap<>();
  private static final Map<String, InEndpoint> inEndpointRegistry = new HashMap<>();
  private static final Map<String, InEndpoint> testingInEndpointRegistry = new HashMap<>();
  private static final Map<String, Endpoint> testingOutEndpointRegistry = new HashMap<>();

  private static final String STATE_ACTUAL = "actual";
  private static final String STATE_TESTING = "testing";
  private static String state = STATE_ACTUAL;

  private CentralEndpointsRegister() {}

  public static Endpoint getOutEndpoint(String endpointId) {
    if (STATE_ACTUAL.equals(state)) {
      return outEndpointRegistry.get(endpointId);
    }
    return testingOutEndpointRegistry.getOrDefault(endpointId, outEndpointRegistry.get(endpointId));
  }

  public static InEndpoint getInEndpoint(String endpointId) {
    if (STATE_ACTUAL.equals(state)) {
      return inEndpointRegistry.get(endpointId);
    }
    return testingInEndpointRegistry.get(endpointId);
  }

  public static void put(String endpointId, OutEndpoint endpoint) {
    outEndpointRegistry.put(endpointId, endpoint);
    testingOutEndpointRegistry.put(endpointId, toTestEndpoint(endpoint));
  }

  public static void put(String id, InEndpoint inEndpoint) {
    inEndpointRegistry.put(id, inEndpoint);
    testingInEndpointRegistry.put(id, toTestEndpoint(inEndpoint));
  }

  public static Endpoint getCamelEndpoint(String uri) {
    return camelContext().getEndpoint(uri);
  }

  private static InEndpoint toTestEndpoint(InEndpoint inEndpoint) {
    if (inEndpoint instanceof RestInEndpoint) {
      return new RestInEndpoint(
          inEndpoint.getUri() + TESTING_SUFFIX, inEndpoint.getId());
    }
    return new InEndpoint(modifyUriForTestRoute(inEndpoint.getUri()), inEndpoint.getId());
  }

  private static OutEndpoint toTestEndpoint(OutEndpoint outEndpoint) {
    return new OutEndpoint(
        modifyUriForTestRoute(outEndpoint.getEndpointUri()),
        outEndpoint.getEndpointId() + TESTING_SUFFIX);
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

    sb.append(TESTING_SUFFIX).append(uriOptions);
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

  public static void putInTestingState() {
    state = STATE_TESTING;
  }

  public static void putInActualState() {
    state = STATE_ACTUAL;
  }

  public static String suffixForCurrentState() {
    return state.equals(STATE_TESTING) ? TESTING_SUFFIX : StringUtils.EMPTY;
  }
}
