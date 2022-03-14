package de.ikor.sip.foundation.testkit.util;

import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.component.rest.RestEndpoint;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.stereotype.Service;

/** Helper Service which resolves an entrypoint URI for a route during runtime. */
@Service
@RequiredArgsConstructor
public class SIPEndpointResolver {

  private static final String CONTEXT_PATH_SUFFIX = "/*";
  private final ServletRegistrationBean<?> camelServletRegistrationBean;
  private final CamelContext camelContext;

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
    return endpointURI;
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
    String path = camelServletRegistrationBean.getUrlMappings().stream().findFirst().orElse("");
    return path.replace(CONTEXT_PATH_SUFFIX, "");
  }
}
