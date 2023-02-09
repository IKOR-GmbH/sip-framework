package de.ikor.sip.foundation.core.apps.declarative;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.declarative.annonations.InboundConnector;
import de.ikor.sip.foundation.core.declarative.annonations.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annonations.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroup;
import de.ikor.sip.foundation.core.declarative.connectors.GenericInboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connectors.GenericOutboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connectors.RestConnectorBase;
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

  @InboundConnector(
      connectorId = "appendStaticMessageProvider",
      belongsToGroup = SIP1,
      toScenario = APPEND_STATIC_MESSAGE_SCENARIO)
  public class AppendStaticMessageProvider extends GenericInboundConnectorBase {

    @Override
    public DirectEndpointConsumerBuilder getInboundEndpoint() {
      return StaticEndpointBuilders.direct("triggerAdapter-append");
    }

    @Override
    protected void configureEndpointRoute(RouteDefinition definition) {
      definition.setBody(exchange -> "PRODUCED-" + exchange.getIn().getBody());
    }

    @Override
    public void configureAfterResponse(RouteDefinition definition) {
      definition.setBody(exchange -> exchange.getIn().getBody() + "-Handled");
    }
  }

  @OutboundConnector(
      connectorId = "appendStaticMessageConsumer",
      belongsToGroup = SIP2,
      fromScenario = APPEND_STATIC_MESSAGE_SCENARIO)
  public class AppendStaticMessageConsumer extends GenericOutboundConnectorBase {

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

  @InboundConnector(belongsToGroup = SIP1, toScenario = "RestDSL")
  public class RestConnectorTestBase extends RestConnectorBase {

    @Override
    protected void configureRest(RestDefinition definition) {
      definition.post("path").type(String.class).get("path");
    }

    @Override
    protected void defineRequestRoute(final RouteDefinition definition) {

    }

    @Override
    protected void defineResponseRoute(final RouteDefinition definition) {
      super.defineResponseRoute(definition);
    }
  }


  @OutboundConnector(belongsToGroup = SIP2, fromScenario = "RestDSL")
  public class RestScenarioConsumer extends GenericOutboundConnectorBase {

    @Override
    public EndpointProducerBuilder getOutboundEndpoint() {
      return StaticEndpointBuilders.log("message");
    }

    @Override
    protected void configureEndpointRoute(RouteDefinition definition) {
      definition.setBody(exchange -> exchange.getIn().getBody() + "-CONSUMED");
    }

    @Override
    public void configureAfterResponse(RouteDefinition definition) {
      definition.setBody(exchange -> exchange.getIn().getBody() + "-Handled-Outbound");
    }
  }
  // <---- RestDSL SCENARIO
  @de.ikor.sip.foundation.core.declarative.annonations.ConnectorGroup(groupId = SIP1)
  public class ConnectorGroupSip1 extends ConnectorGroup {}

  @de.ikor.sip.foundation.core.declarative.annonations.ConnectorGroup(groupId = SIP2)
  public class ConnectorGroupSip2 extends ConnectorGroup {}
}
