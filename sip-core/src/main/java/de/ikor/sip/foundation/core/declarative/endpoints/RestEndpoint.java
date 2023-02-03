package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.RestEndpointOrchestrationInfo;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;

public abstract class RestEndpoint extends AnnotatedInboundEndpoint
    implements InboundEndpointDefinition {

  @Override
  public EndpointConsumerBuilder getInboundEndpoint() {
    return null;
  }

  @Override
  public void doOrchestrate(EndpointOrchestrationInfo data) {
    configureRest(((RestEndpointOrchestrationInfo) data).getRestDefinition());
    super.doOrchestrate(data);
  }

  protected abstract void configureRest(final RestDefinition definition);

  @Override
  protected abstract void configureEndpointRoute(final RouteDefinition definition);
}
