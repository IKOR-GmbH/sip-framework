package de.ikor.sip.foundation.core.apps.declarative;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.declarative.annonations.Connector;
import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.annonations.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annonations.OutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.connectors.AnnotatedConnector;
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

  private static final String SIP1 = "SIP1";
  private static final String SIP2 = "SIP2";
  private static final String APPEND_STATIC_MESSAGE_SCENARIO = "AppendStaticMessage";

  // ----> AppendStaticMessage SCENARIO
  @IntegrationScenario(scenarioId = APPEND_STATIC_MESSAGE_SCENARIO, requestModel = String.class)
  public class AppendStaticMessageScenario extends AnnotatedScenario {}

  @InboundEndpoint(
      endpointId = "appendStaticMessageProvider",
      belongsToConnector = SIP1,
      providesToScenario = APPEND_STATIC_MESSAGE_SCENARIO)
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

  @OutboundEndpoint(endpointId = "appendStaticMessageConsumer",belongsToConnector = SIP2, consumesFromScenario = APPEND_STATIC_MESSAGE_SCENARIO)
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

  @InboundEndpoint(belongsToConnector = SIP1, providesToScenario = "RestDSL")
  public class RestEndpointTest extends RestEndpoint {

    @Override
    protected void configureRest(RestDefinition definition) {
      definition.post("path").type(String.class).get("path");
    }

    @Override
    protected void configureEndpointRoute(RouteDefinition definition) {
      definition.setBody(exchange -> "PRODUCED_REST-" + exchange.getIn().getBody());
    }
  }

  @OutboundEndpoint(belongsToConnector = SIP2, consumesFromScenario = "RestDSL")
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
  @Connector(connectorId = SIP1)
  public class ConnectorSip1 extends AnnotatedConnector {}

  @Connector(connectorId = SIP2)
  public class ConnectorSip2 extends AnnotatedConnector {}
}
