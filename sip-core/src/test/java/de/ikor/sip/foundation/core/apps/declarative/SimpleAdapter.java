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

  @IntegrationScenario(scenarioId = "Passthrough", requestModel = String.class)
  class PassthroughScenario extends AnnotatedScenario {}

  @InboundEndpoint(belongsToConnector = "SIP1", providesToScenario = "Passthrough")
  class PassthroughProvider extends AnnotatedInboundEndpoint {

    @Override
    public DirectEndpointConsumerBuilder getInboundEndpoint() {
      return StaticEndpointBuilders.direct("trigger-passthrough");
    }
  }

  @OutboundEndpoint(belongsToConnector = "SIP2", consumesFromScenario = "Passthrough")
  class PassthroughCosumer extends AnnotatedOutboundEndpoint {

    @Override
    public EndpointProducerBuilder getOutboundEndpoint() {
      return StaticEndpointBuilders.log("message");
    }
  }

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

  @InboundEndpoint(belongsToConnector = "SIP1", providesToScenario = "AppendStaticMessage")
  public class RestEndpointTest extends RestEndpoint {

    @Override
    protected void configureRest(RestDefinition definition) {
      definition.post("path").type(String.class);
    }

    @Override
    protected void configureEndpointRoute(RouteDefinition definition) {
      definition.setBody(exchange -> "PRODUCED_REST-" + exchange.getIn().getBody());
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

  @IntegrationScenario(
      scenarioId = "CDMValidation",
      requestModel = CDMRequest.class,
      responseModel = CDMResponse.class)
  public class CDMValidationScenario extends AnnotatedScenario {}

  @InboundEndpoint(belongsToConnector = "SIP1", providesToScenario = "CDMValidation")
  public class InboundCDMEndpoint extends AnnotatedInboundEndpoint {

    @Override
    public DirectEndpointConsumerBuilder getInboundEndpoint() {
      return StaticEndpointBuilders.direct("cdm-validator");
    }
  }

  @OutboundEndpoint(belongsToConnector = "SIP2", consumesFromScenario = "CDMValidation")
  public class OutboundCDMEndpoint extends AnnotatedOutboundEndpoint {

    @Override
    public EndpointProducerBuilder getOutboundEndpoint() {
      return StaticEndpointBuilders.log("message");
    }

    @Override
    protected void configureEndpointRoute(RouteDefinition definition) {
      definition.process(
          exchange -> {
            CDMRequest request = exchange.getMessage().getBody(CDMRequest.class);
            CDMResponse response = new CDMResponse("ID: " + request.getId());
            exchange.getMessage().setBody(request.getId() == 1000 ? response : "Wrong CDM type");
          });
    }
  }
}
