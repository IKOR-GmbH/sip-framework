package de.ikor.sip.foundation.core.framework;

import org.apache.camel.Endpoint;

import java.util.HashMap;
import java.util.Map;

public class CentralOutEndpointsRegister {
    private static Map<String, Endpoint> registry = new HashMap<>();
    private CentralOutEndpointsRegister(){}

    public static Endpoint getEndpoint(String endpointId) {
        return registry.get(endpointId);
    }

    public static void put(String endpointId, Endpoint endpoint) {
        registry.put(endpointId, endpoint);
    }
}
