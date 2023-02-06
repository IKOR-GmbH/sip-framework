package de.ikor.sip.foundation.core.declarative.endpoints;

import de.ikor.sip.foundation.core.declarative.orchestation.EndpointOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestation.RestEndpointOrchestrationInfo;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;

public abstract class RestEndpoint extends AnnotatedInboundEndpoint
    implements InboundEndpointDefinition {

  private static final String REST_DIRECT_PATH = "rest";

  @Override
  public EndpointConsumerBuilder getInboundEndpoint() {
    return StaticEndpointBuilders.direct(REST_DIRECT_PATH + getProvidedScenario().getID());
  }

  @Override
  public void doOrchestrate(EndpointOrchestrationInfo data) {
    RestEndpointOrchestrationInfo orchestrationInfo = (RestEndpointOrchestrationInfo) data;
    configureRest(orchestrationInfo.getRestDefinition());
    super.doOrchestrate(data);
    prependRestRoute(orchestrationInfo);
  }

  protected abstract void configureRest(final RestDefinition definition);

  @Override
  protected abstract void configureEndpointRoute(final RouteDefinition definition);

  private void prependRestRoute(RestEndpointOrchestrationInfo orchestrationInfo) {
    RestDefinition restDefinition = orchestrationInfo.getRestDefinition();
    if (!restDefinition.getVerbs().isEmpty()) {
      // restDefinition.getVerbs().forEach(verbDefinition -> verbDefinition.setTo(new
      // ToDefinition(getInboundEndpoint().getUri())));
      restDefinition.to(getInboundEndpoint().getUri());
    }
  }
}
