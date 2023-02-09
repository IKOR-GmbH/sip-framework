package de.ikor.sip.foundation.core.apps.declarative;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.connector.GenericInboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connector.GenericOutboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connector.RestConnectorBase;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupBase;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioBase;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;

@SIPIntegrationAdapter
public class SimpleAdapter {

    private static final String SIP1 = "SIP1";
    private static final String SIP2 = "SIP2";
    private static final String APPEND_STATIC_MESSAGE_SCENARIO = "AppendStaticMessage";

    // ----> AppendStaticMessage SCENARIO
    @IntegrationScenario(scenarioId = APPEND_STATIC_MESSAGE_SCENARIO, requestModel = String.class)
    public class AppendStaticMessageScenario extends IntegrationScenarioBase {
    }

    @InboundConnector(
            connectorId = "appendStaticMessageProvider",
            belongsToGroup = SIP1,
            toScenario = APPEND_STATIC_MESSAGE_SCENARIO)
    public class AppendStaticMessageProvider extends GenericInboundConnectorBase {

        @Override
        protected void defineRequestRoute(final RouteDefinition definition) {
            definition.setBody(exchange -> "PRODUCED-" + exchange.getIn().getBody());
        }

        @Override
        protected void defineResponseRoute(final RouteDefinition definition) {
            definition.setBody(exchange -> exchange.getIn().getBody() + "-Handled");
        }

        @Override
        protected EndpointConsumerBuilder defineInitiatingEndpoint() {
            return StaticEndpointBuilders.direct("triggerAdapter-append");
        }
    }

    @OutboundConnector(
            connectorId = "appendStaticMessageConsumer",
            belongsToGroup = SIP2,
            fromScenario = APPEND_STATIC_MESSAGE_SCENARIO)
    public class AppendStaticMessageConsumer extends GenericOutboundConnectorBase {

        @Override
        protected void defineRequestRoute(final RouteDefinition definition) {
            definition.setBody(exchange -> exchange.getIn().getBody() + "-CONSUMED");
        }

        @Override
        protected EndpointProducerBuilder defineOutgoingEndpoint() {
            return StaticEndpointBuilders.log("message");
        }
    }
    // <---- AppendStaticMessage SCENARIO

    // ----> RestDSL SCENARIO
    @IntegrationScenario(scenarioId = "RestDSL", requestModel = String.class)
    public class RestDSLScenario extends IntegrationScenarioBase {
    }

    @InboundConnector(belongsToGroup = SIP1, toScenario = "RestDSL")
    public class RestConnectorTestBase extends RestConnectorBase {

        @Override
        protected void configureRest(RestDefinition definition) {
            definition.post("path").type(String.class).get("path");
        }

        @Override
        protected void defineRequestRoute(final RouteDefinition definition) {

        }
    }

    @OutboundConnector(belongsToGroup = SIP2, fromScenario = "RestDSL")
    public class RestScenarioConsumer extends GenericOutboundConnectorBase {

        @Override
        protected void defineRequestRoute(final RouteDefinition definition) {
            definition.setBody(exchange -> exchange.getIn().getBody() + "-CONSUMED");
        }

        @Override
        protected void defineResponseRoute(final RouteDefinition definition) {
            definition.setBody(exchange -> exchange.getIn().getBody() + "-Handled-Outbound");
        }

        @Override
        protected EndpointProducerBuilder defineOutgoingEndpoint() {
            return StaticEndpointBuilders.log("message");
        }
    }

    // <---- RestDSL SCENARIO
    @de.ikor.sip.foundation.core.declarative.annonation.ConnectorGroup(groupId = SIP1)
    public class ConnectorGroupSip1 extends ConnectorGroupBase {
    }

    @de.ikor.sip.foundation.core.declarative.annonation.ConnectorGroup(groupId = SIP2)
    public class ConnectorGroupSip2 extends ConnectorGroupBase {
    }
}
