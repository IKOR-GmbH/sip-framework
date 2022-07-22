package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.rest.RestEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Class for triggering Camel REST route */
@Component
@RequiredArgsConstructor
public class RestRouteInvoker implements RouteInvoker {

  private final ProducerTemplate producerTemplate;

  private Endpoint endpoint;

  @Value("${sip.adapter.camel-endpoint-context-path}")
  private String contextPath = "";

  @Override
  public Exchange invoke(Exchange exchange) {
    return producerTemplate.send(extractRESTEndpointURI((RestEndpoint) endpoint), exchange);
  }

  @Override
  public boolean matchEndpoint(Endpoint endpoint) {
    return endpoint instanceof RestEndpoint;
  }

  @Override
  public RouteInvoker setEndpoint(Endpoint endpoint) {
    this.endpoint = endpoint;
    return this;
  }

  private String extractRESTEndpointURI(RestEndpoint restEndpoint) {
    return "rest:" + restEndpoint.getMethod() + ":" + resolveContextPath() + restEndpoint.getPath();
  }

  private String resolveContextPath() {
    return contextPath.replaceAll("/[*]$", "");
  }
}
