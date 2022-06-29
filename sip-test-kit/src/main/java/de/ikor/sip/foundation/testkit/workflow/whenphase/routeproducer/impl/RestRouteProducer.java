package de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.impl;

import de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer.RouteProducer;
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
public class RestRouteProducer implements RouteProducer {

  private static final String CONTEXT_PATH_SUFFIX = "[/][*]$";

  private final ProducerTemplate producerTemplate;

  @Value("${sip.adapter.camel-endpoint-context-path}")
  private String contextPath = "";

  @Override
  public Exchange executeTask(Exchange exchange, Endpoint endpoint) {
    return producerTemplate.send(extractRESTEndpointURI((RestEndpoint) endpoint), exchange);
  }

  private String extractRESTEndpointURI(RestEndpoint restEndpoint) {
    return "rest:" + restEndpoint.getMethod() + ":" + resolveContextPath() + restEndpoint.getPath();
  }

  private String resolveContextPath() {
    return contextPath.replaceAll(CONTEXT_PATH_SUFFIX, "");
  }
}
