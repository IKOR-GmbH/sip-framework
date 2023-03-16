package de.ikor.sip.foundation.core.apps.declarative;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.connector.GenericInboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connector.GenericOutboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioBase;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

@SIPIntegrationAdapter
@ComponentScan(excludeFilters = @Filter(SIPIntegrationAdapter.class))
public class DeclarativeStructureAdapter {

  private static final String DUMMY_GROUP1 = "DUMMY_GROUP1";
  private static final String DUMMY_GROUP2 = "DUMMY_GROUP2";
  private static final String DUMMY_GROUP3 = "DUMMY_GROUP3";

  @IntegrationScenario(scenarioId = DummyScenario.ID, requestModel = String.class)
  public class DummyScenario extends IntegrationScenarioBase {
    public static final String ID = "dummyScenario";
  }

  @InboundConnector(
      connectorId = "inboundConnector",
      connectorGroup = DUMMY_GROUP1,
      integrationScenario = DummyScenario.ID,
      requestModel = String.class,
      responseModel = String.class)
  public class DummyInboundConnector extends GenericInboundConnectorBase {
    @Override
    protected EndpointConsumerBuilder defineInitiatingEndpoint() {
      return StaticEndpointBuilders.direct("dummyInput");
    }
  }

  @OutboundConnector(
      connectorId = "outboundConnectorOne",
      connectorGroup = DUMMY_GROUP2,
      integrationScenario = DummyScenario.ID,
      requestModel = String.class)
  public class OutboundConnectorOne extends GenericOutboundConnectorBase {
    @Override
    protected EndpointProducerBuilder defineOutgoingEndpoint() {
      return StaticEndpointBuilders.log("messageGroup2");
    }
  }

  @OutboundConnector(
      connectorId = "outboundConnectorTwo",
      connectorGroup = DUMMY_GROUP3,
      integrationScenario = DummyScenario.ID,
      requestModel = String.class)
  public class OutboundConnectorTwo extends GenericOutboundConnectorBase {
    @Override
    protected EndpointProducerBuilder defineOutgoingEndpoint() {
      return StaticEndpointBuilders.log("messageGroup3");
    }
  }
}
