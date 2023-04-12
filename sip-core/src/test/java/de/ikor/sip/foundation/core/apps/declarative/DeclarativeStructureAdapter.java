package de.ikor.sip.foundation.core.apps.declarative;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.declarative.annonation.InboundConnector;
import de.ikor.sip.foundation.core.declarative.annonation.IntegrationScenario;
import de.ikor.sip.foundation.core.declarative.annonation.OutboundConnector;
import de.ikor.sip.foundation.core.declarative.connector.GenericInboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.connector.GenericOutboundConnectorBase;
import de.ikor.sip.foundation.core.declarative.orchestration.Orchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.connector.ConnectorOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.connector.ConnectorOrchestrator;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrationInfo;
import de.ikor.sip.foundation.core.declarative.orchestration.scenario.ScenarioOrchestrator;
import de.ikor.sip.foundation.core.declarative.scenario.IntegrationScenarioBase;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.EndpointProducerBuilder;
import org.apache.camel.builder.endpoint.StaticEndpointBuilders;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

@SIPIntegrationAdapter
@ComponentScan(excludeFilters = @Filter(SIPIntegrationAdapter.class))
public class DeclarativeStructureAdapter {

  private final String GROUP_ID = "group";

  @Data
  @AllArgsConstructor
  public static class ScenarioResponse {

    private String id;

    private Integer value;
  }

  @IntegrationScenario(
      scenarioId = Scenario.ID,
      requestModel = String.class,
      responseModel = ScenarioResponse.class)
  public class Scenario extends IntegrationScenarioBase {

    public static final String ID = "TestScenario";

    @Override
    public Orchestrator<ScenarioOrchestrationInfo> getOrchestrator() {
      return ScenarioOrchestrator.forOrchestrationDsl(
          dsl -> {
            dsl.forInboundConnectors(InboundConnectorOne.ID)
                .callOutboundConnector(OutboundConnectorOne.ID)
                .withRequestPreparation(
                    context -> context.getOriginalRequest() + "-scenarioprepared")
                .andNoResponseHandling();
            dsl.forInboundConnectors(InboundConnectorTwo.class)
                .callOutboundConnector(OutboundConnectorTwo.class)
                .withRequestPreparation(
                    context -> context.getOriginalRequest() + "-scenarioprepared")
                .andNoResponseHandling()
                .callOutboundConnector(OutboundConnectorOne.class)
                .andHandleResponse(
                    (latestResponse, context) -> {
                      List<Integer> valueResponses =
                          context.getOrchestrationStepResponses().stream()
                              .map(step -> ((ScenarioResponse) step.result()).getValue())
                              .toList();
                      Integer sum =
                          IntStream.range(0, valueResponses.size())
                              .map(i -> valueResponses.get(i) * (int) Math.pow(10, i + 1))
                              .sum();
                      ((ScenarioResponse) latestResponse).setValue(sum);
                      ((ScenarioResponse) latestResponse).setId("scenario-handled-response");
                    });
          });
    }
  }

  @InboundConnector(
      connectorId = InboundConnectorOne.ID,
      connectorGroup = GROUP_ID,
      integrationScenario = Scenario.ID,
      requestModel = String.class,
      responseModel = ScenarioResponse.class)
  public class InboundConnectorOne extends GenericInboundConnectorBase {

    public static final String ID = "inboundConnectorOne";

    @Override
    protected EndpointConsumerBuilder defineInitiatingEndpoint() {
      return StaticEndpointBuilders.direct("dummyInputOne");
    }
  }

  @InboundConnector(
      connectorId = InboundConnectorTwo.ID,
      connectorGroup = GROUP_ID,
      integrationScenario = Scenario.ID,
      requestModel = String.class,
      responseModel = ScenarioResponse.class)
  public class InboundConnectorTwo extends GenericInboundConnectorBase {

    public static final String ID = "inboundConnectorTwo";

    @Override
    protected EndpointConsumerBuilder defineInitiatingEndpoint() {
      return StaticEndpointBuilders.direct("dummyInputTwo");
    }
  }

  @OutboundConnector(
      connectorId = OutboundConnectorOne.ID,
      connectorGroup = GROUP_ID,
      integrationScenario = Scenario.ID,
      requestModel = String.class,
      responseModel = ScenarioResponse.class)
  public class OutboundConnectorOne extends GenericOutboundConnectorBase {

    public static final String ID = "outboundConnectorOne";

    @Override
    protected EndpointProducerBuilder defineOutgoingEndpoint() {
      return StaticEndpointBuilders.log("messageConnector1");
    }

    @Override
    protected Orchestrator<ConnectorOrchestrationInfo> defineTransformationOrchestrator() {
      return ConnectorOrchestrator.forConnector(this)
          .setResponseRouteTransformer(
              routeDefinition ->
                  routeDefinition.setBody().constant(new ScenarioResponse("testOne", 1)));
    }
  }

  @OutboundConnector(
      connectorId = OutboundConnectorTwo.ID,
      connectorGroup = GROUP_ID,
      integrationScenario = Scenario.ID,
      requestModel = String.class,
      responseModel = ScenarioResponse.class)
  public class OutboundConnectorTwo extends GenericOutboundConnectorBase {

    public static final String ID = "outboundConnectorTwo";

    @Override
    protected EndpointProducerBuilder defineOutgoingEndpoint() {
      return StaticEndpointBuilders.log("messageConnector2");
    }

    @Override
    protected Orchestrator<ConnectorOrchestrationInfo> defineTransformationOrchestrator() {
      return ConnectorOrchestrator.forConnector(this)
          .setResponseRouteTransformer(
              routeDefinition ->
                  routeDefinition.setBody().constant(new ScenarioResponse("testTwo", 2)));
    }
  }
}
