package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.rest.RestEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Invoker class for triggering Camel REST route */
@Component
@RequiredArgsConstructor
public class RestRouteInvoker implements RouteInvoker {

  private final ProducerTemplate producerTemplate;

  @Value("${sip.adapter.camel-endpoint-context-path}")
  private String contextPath = "";

  @Override
  public Exchange invoke(Exchange exchange, Endpoint endpoint) {
    return producerTemplate.send(extractRESTEndpointURI((RestEndpoint) endpoint), exchange);
  }

  @Override
  public boolean isApplicable(Endpoint endpoint) {
    return endpoint instanceof RestEndpoint;
  }

  private String extractRESTEndpointURI(RestEndpoint restEndpoint) {
    return "rest:" + restEndpoint.getMethod() + ":" + resolveContextPath() + restEndpoint.getPath();
  }

  private String resolveContextPath() {
    return contextPath.replaceAll("/[*]$", "");
  }
}
