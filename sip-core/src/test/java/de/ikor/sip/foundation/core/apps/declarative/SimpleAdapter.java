package de.ikor.sip.foundation.core.apps.declarative;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.annonations.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annonations.OutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.AnnotatedInboundEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.AnnotatedOutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.RestEndpoint;
import de.ikor.sip.foundation.core.declarative.scenario.AnnotatedScenario;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.builder.endpoint.dsl.DirectEndpointBuilderFactory.DirectEndpointConsumerBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;

@SIPIntegrationAdapter
public class SimpleAdapter {
  // ----> AppendStaticMessage SCENARIO
  @IntegrationScenario(scenarioId = "AppendStaticMessage", requestModel = String.class)
  public class AppendStaticMessageScenario extends AnnotatedScenario {}

  @InboundEndpoint(belongsToConnector = "SIP1", providesToScenario = "AppendStaticMessage")
  public class AppendStaticMessageProvider extends AnnotatedInboundEndpoint {

    @Override
    public DirectEndpointConsumerBuilder getInboundEndpoint() {
      return StaticEndpointBuilders.direct("triggerAdapter-append");
    }

    @Override
    protected void configureEndpointRoute(RouteDefinition definition) {
      definition.setBody(exchange -> "PRODUCED-" + exchange.getIn().getBody());
    }
  }

  @OutboundEndpoint(belongsToConnector = "SIP2", consumesFromScenario = "AppendStaticMessage")
  public class AppendStaticMessageConsumer extends AnnotatedOutboundEndpoint {

    @Override
    public EndpointProducerBuilder getOutboundEndpoint() {
      return StaticEndpointBuilders.log("message");
    }

    @Override
    protected void configureEndpointRoute(RouteDefinition definition) {
      definition.setBody(exchange -> exchange.getIn().getBody() + "-CONSUMED");
    }
  }
  // <---- AppendStaticMessage SCENARIO

  // ----> RestDSL SCENARIO
  @IntegrationScenario(scenarioId = "RestDSL", requestModel = String.class)
  public class RestDSLScenario extends AnnotatedScenario {}

  @InboundEndpoint(belongsToConnector = "SIP1", providesToScenario = "RestDSL")
  public class RestEndpointTest extends RestEndpoint {

    @Override
    protected void configureRest(RestDefinition definition) {
      definition.post("path").type(String.class).get("path");
    }

    @Override
    protected void configureEndpointRoute(RouteDefinition definition) {
      definition.setBody(exchange -> "PRODUCED_REST-" + exchange.getIn().getBody());
    }

    @Override
    public void configureAfterResponse(RouteDefinition definition) {
      definition.setBody(exchange -> exchange.getIn().getBody() + "-Handled");
    }
  }

  @OutboundEndpoint(belongsToConnector = "SIP2", consumesFromScenario = "RestDSL")
  public class RestScenarioConsumer extends AnnotatedOutboundEndpoint {

    @Override
    public EndpointProducerBuilder getOutboundEndpoint() {
      return StaticEndpointBuilders.log("message");
    }

    @Override
    protected void configureEndpointRoute(RouteDefinition definition) {
      definition.setBody(exchange -> exchange.getIn().getBody() + "-CONSUMED");
    }
  }
  // <---- RestDSL SCENARIO
}
