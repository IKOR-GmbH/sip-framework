package de.ikor.sip.foundation.core.apps.declarative;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.declarative.annonations.Connector;
import de.ikor.sip.foundation.core.declarative.annonations.InboundEndpoint;
import de.ikor.sip.foundation.core.declarative.annonations.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annonations.OutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.connectors.AnnotatedConnector;
import de.ikor.sip.foundation.core.declarative.endpoints.AnnotatedInboundEndpoint;
import de.ikor.sip.foundation.core.declarative.endpoints.AnnotatedOutboundEndpoint;
import de.ikor.sip.foundation.core.declarative.scenario.AnnotatedScenario;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.builder.endpoint.dsl.DirectEndpointBuilderFactory.DirectEndpointConsumerBuilder;
import org.apache.camel.model.RouteDefinition;

@SIPIntegrationAdapter
public class SimpleAdapter {

  @IntegrationScenario(scenarioId = "Passthrough", requestModel = String.class)
  class PassthroughScenario extends AnnotatedScenario {}

  @InboundEndpoint(
      endpointId = "passthroughProvider",
      belongsToConnector = "SIP1",
      providesToScenario = "Passthrough")
  class PassthroughProvider extends AnnotatedInboundEndpoint {

    @Override
    public DirectEndpointConsumerBuilder getInboundEndpoint() {
      return StaticEndpointBuilders.direct("trigger-passthrough");
    }
  }

  @OutboundEndpoint(
      endpointId = "passthroughCosumer",
      belongsToConnector = "SIP2",
      consumesFromScenario = "Passthrough")
  class PassthroughCosumer extends AnnotatedOutboundEndpoint {

    @Override
    public EndpointProducerBuilder getOutboundEndpoint() {
      return StaticEndpointBuilders.log("message");
    }
  }

  @IntegrationScenario(scenarioId = "AppendStaticMessage", requestModel = String.class)
  public class AppendStaticMessageScenario extends AnnotatedScenario {}

  @InboundEndpoint(
      endpointId = "appendStaticMessageProvider",
      belongsToConnector = "SIP1",
      providesToScenario = "AppendStaticMessage")
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

  @OutboundEndpoint(
      endpointId = "appendStaticMessageConsumer",
      belongsToConnector = "SIP2",
      consumesFromScenario = "AppendStaticMessage")
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

  @Connector(connectorId = "SIP1")
  public class ConnectorSip1 extends AnnotatedConnector {}

  @Connector(connectorId = "SIP2")
  public class ConnectorSip2 extends AnnotatedConnector {}
}
