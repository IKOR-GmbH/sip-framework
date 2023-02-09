package de.ikor.sip.foundation.core.apps.declarative;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.declarative.annonations.InboundConnector;
import de.ikor.sip.foundation.core.declarative.annonations.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annonations.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.connectors.GenericInboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connectors.GenericOutboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.scenario.AnnotatedScenario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.apache.camel.builder.endpoint.dsl.DirectEndpointBuilderFactory.DirectEndpointConsumerBuilder;
import org.apache.camel.model.RouteDefinition;

@SIPIntegrationAdapter
public class CDMValidationAdapter {

  @Data
  @AllArgsConstructor
  public static class CDMRequest {
    private int id;
  }

  @Data
  @AllArgsConstructor
  public static class CDMResponse {
    private String id;
  }

  @IntegrationScenario(
      scenarioId = "CDMValidation",
      requestModel = CDMRequest.class,
      responseModel = CDMResponse.class)
  public class CDMValidationScenario extends AnnotatedScenario {}

  @InboundConnector(belongsToGroup = "SIP1", toScenario = "CDMValidation")
  public class InboundCDMConnectorBase extends GenericInboundConnectorBase {

    @Override
    public DirectEndpointConsumerBuilder getInboundEndpoint() {
      return StaticEndpointBuilders.direct("cdm-validator");
    }
  }

  @OutboundConnector(belongsToGroup = "SIP2", fromScenario = "CDMValidation")
  public class OutboundCDMConnector extends GenericOutboundConnectorBase {

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
