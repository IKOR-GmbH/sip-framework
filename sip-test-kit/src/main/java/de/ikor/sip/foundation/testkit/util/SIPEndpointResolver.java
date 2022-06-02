package de.ikor.sip.foundation.testkit.util;

import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.rest.RestEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Helper Service which resolves an entrypoint URI for a route during runtime. */
@Service
@RequiredArgsConstructor
public class SIPEndpointResolver {

  private static final String CONTEXT_PATH_SUFFIX = "[/][*]$";
  private final CamelContext camelContext;

  @Value("${sip.adapter.camel-endpoint-context-path}")
  private String contextPath = "";

  /**
   * Resolve entrypoint URI for the Exchange
   *
   * @param exchange for which the first Endpoint URI (entrypoint) needs to be returned
   * @return resolved URI for the Exchange
   */
  public String resolveURI(Exchange exchange) {
    Endpoint endpoint = resolveEndpoint(exchange);
    String endpointURI = endpoint.getEndpointUri();
    if (endpoint instanceof RestEndpoint) {
      RestEndpoint restEndpoint = (RestEndpoint) endpoint;
      endpointURI = extractRESTEndpointURI(restEndpoint);
    }

    // WIP
//    if (endpoint instanceof CxfEndpoint) {
//      CxfEndpoint cxfEndpoint = (CxfEndpoint) endpoint;
//      endpointURI = extractCXFEndpointURI(cxfEndpoint);
//    }
    return endpointURI;
  }

  // WIP
  public Endpoint resolveCxfEndpoint(Exchange exchange) {
    return resolveEndpoint(exchange);
  }

  private Endpoint resolveEndpoint(Exchange exchange) {
    String routeId = exchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY, String.class);
    Route route = camelContext.getRoute(routeId);
    if (route == null) {
      throw new IllegalArgumentException("Route with id " + routeId + " was not found");
    }
    return route.getEndpoint();
  }

  private String extractRESTEndpointURI(RestEndpoint restEndpoint) {
    return "rest:" + restEndpoint.getMethod() + ":" + resolveContextPath() + restEndpoint.getPath();
  }

  private String resolveContextPath() {
    return contextPath.replaceAll(CONTEXT_PATH_SUFFIX, "");
  }
}
