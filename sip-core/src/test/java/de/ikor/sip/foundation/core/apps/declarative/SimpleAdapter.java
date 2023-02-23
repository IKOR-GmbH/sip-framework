package de.ikor.sip.foundation.core.apps.declarative;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.declarative.annonation.ConnectorGroup;
import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.connector.GenericInboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connector.GenericOutboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connector.RestConnectorBase;
import de.ikor.sip.foundation.core.declarative.connectorgroup.ConnectorGroupBase;
import de.ikor.sip.foundation.core.declarative.orchestration.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.ConnectorOrchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioBase;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

@SIPIntegrationAdapter
@ComponentScan(excludeFilters = @Filter(SIPIntegrationAdapter.class))
public class SimpleAdapter {

  // ----> AppendStaticMessage SCENARIO
  @IntegrationScenario(scenarioId = AppendStaticMessageScenario.ID, requestModel = String.class)
  public class AppendStaticMessageScenario extends IntegrationScenarioBase {
    public static final String ID = "AppendStaticMessage";
  }

  @InboundConnector(
      connectorId = "appendStaticMessageProvider",
      connectorGroup = ConnectorGroupSip1.ID,
      integrationScenario = AppendStaticMessageScenario.ID,
      requestModel = String.class,
      responseModel = String.class)
  public class AppendStaticMessageProvider extends GenericInboundConnectorBase {

    @Override
    protected Orchestrator<ConnectorOrchestrationInfo> defineTransformationOrchestrator() {
      return ConnectorOrchestrator.forConnector(this)
          .setRequestRouteTransformer(this::defineRequestRoute)
          .setResponseRouteTransformer(this::defineResponseRoute);
    }

    protected void defineRequestRoute(final RouteDefinition definition) {
      definition.setBody(exchange -> "PRODUCED-" + exchange.getIn().getBody());
    }

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
      connectorGroup = "SIP2",
      integrationScenario = AppendStaticMessageScenario.ID,
      requestModel = String.class,
      pathToDocumentationResource = "documents/structure/connectors/genericDescription.txt")
  public class AppendStaticMessageConsumer extends GenericOutboundConnectorBase {

    @Override
    protected Orchestrator<ConnectorOrchestrationInfo> defineTransformationOrchestrator() {
      return ConnectorOrchestrator.forConnector(this)
          .setRequestRouteTransformer(
              definition ->
                  definition.setBody(exchange -> exchange.getIn().getBody() + "-CONSUMED"));
    }

    @Override
    protected EndpointProducerBuilder defineOutgoingEndpoint() {
      return StaticEndpointBuilders.log("message");
    }
  }
  // <---- AppendStaticMessage SCENARIO

  // ----> RestDSL SCENARIO
  @IntegrationScenario(scenarioId = RestDSLScenario.ID, requestModel = String.class)
  public class RestDSLScenario extends IntegrationScenarioBase {
    public static final String ID = "RestDSL";
  }

  @InboundConnector(
      connectorGroup = ConnectorGroupSip1.ID,
      integrationScenario = RestDSLScenario.ID,
      requestModel = String.class)
  public class RestConnectorTestBase extends RestConnectorBase {

    @Override
    protected void configureRest(RestDefinition definition) {
      definition.post("path").type(String.class).get("path");
    }

    @Override
    protected Orchestrator<ConnectorOrchestrationInfo> defineTransformationOrchestrator() {
      return ConnectorOrchestrator.forConnector(this)
          .setRequestRouteTransformer(this::defineRequestRoute);
    }

    protected void defineRequestRoute(final RouteDefinition definition) {
      definition.setBody(exchange -> "PRODUCED_REST-" + exchange.getIn().getBody());
    }
  }

  @OutboundConnector(
      connectorGroup = "SIP2",
      integrationScenario = RestDSLScenario.ID,
      requestModel = String.class,
      responseModel = String.class)
  public class RestScenarioConsumer extends GenericOutboundConnectorBase {

    @Override
    protected Orchestrator<ConnectorOrchestrationInfo> defineTransformationOrchestrator() {
      return ConnectorOrchestrator.forConnector(this)
          .setRequestRouteTransformer(this::defineRequestRoute)
          .setResponseRouteTransformer(this::defineResponseRoute);
    }

    protected void defineRequestRoute(final RouteDefinition definition) {
      definition.setBody(exchange -> exchange.getIn().getBody() + "-CONSUMED");
    }

    protected void defineResponseRoute(final RouteDefinition definition) {
      definition.setBody(exchange -> exchange.getIn().getBody() + "-Handled-Outbound");
    }

    @Override
    protected EndpointProducerBuilder defineOutgoingEndpoint() {
      return StaticEndpointBuilders.log("message");
    }
  }

  // <---- RestDSL SCENARIO
  @ConnectorGroup(groupId = ConnectorGroupSip1.ID)
  public class ConnectorGroupSip1 extends ConnectorGroupBase {
    public static final String ID = "SIP1";
  }
}
