package de.ikor.sip.foundation.core.declarative;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.ikor.sip.foundation.core.apps.declarative.ScenarioOrchestrationAdapter;
import de.ikor.sip.foundation.core.apps.declarative.ScenarioOrchestrationAdapter.AutoOrchestratedOutboundConnectorOne;
import de.ikor.sip.foundation.core.apps.declarative.ScenarioOrchestrationAdapter.AutoOrchestratedOutboundConnectorTwo;
import de.ikor.sip.foundation.core.apps.declarative.ScenarioOrchestrationAdapter.ScenarioResponse;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.DisableJmx;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@CamelSpringBootTest
@SpringBootTest(classes = {ScenarioOrchestrationAdapter.class})
@DisableJmx(false)
@MockEndpoints("log:message*")
@DirtiesContext
class ScenarioOrchestrationTest {

  @Autowired private FluentProducerTemplate template;

  @EndpointInject("mock:log:messageConnector1")
  private MockEndpoint mockedLoggerConnector1;

  @EndpointInject("mock:log:messageConnector2")
  private MockEndpoint mockedLoggerConnector2;

  @BeforeEach
  void setup() {
    mockedLoggerConnector1.reset();
    mockedLoggerConnector2.reset();
  }

  @AfterEach
  void assertLoggers() throws InterruptedException {
    mockedLoggerConnector1.assertIsSatisfied();
    mockedLoggerConnector2.assertIsSatisfied();
  }

  @Test
  void WHEN_callingFirstInboundConnector_THEN_OnlyFirstOutboundConnectorReceivesAMessage() {
    // arrange
    mockedLoggerConnector1.expectedMessageCount(1);
    mockedLoggerConnector1.expectedBodiesReceived("Hi Adapter-scenarioprepared");
    mockedLoggerConnector2.expectedMessageCount(0);

    // act
    Exchange exchange = template.withBody("Hi Adapter").to(direct("dummyInputOne")).send();

    // assert
    assertThat(exchange.getMessage().getBody()).isInstanceOf(ScenarioResponse.class);
    assertThat(exchange.getMessage().getBody(ScenarioResponse.class).getValue()).isEqualTo(1);
  }

  @Test
  void
      WHEN_callingSecondInboundConnector_THEN_OutboundConnector1And2ReceiveAMessageAndResponseIsAggregated() {
    // arrange
    mockedLoggerConnector1.expectedMessageCount(1);
    mockedLoggerConnector1.expectedBodiesReceived("Hi Adapter");
    mockedLoggerConnector2.expectedMessageCount(1);
    mockedLoggerConnector2.expectedBodiesReceived("Hi Adapter-scenarioprepared");

    // act
    Exchange exchange = template.withBody("Hi Adapter").to(direct("dummyInputTwo")).send();

    // assert
    assertThat(exchange.getMessage().getBody()).isInstanceOf(ScenarioResponse.class);
    assertThat(exchange.getMessage().getBody(ScenarioResponse.class).getValue()).isEqualTo(120);
    assertThat(exchange.getMessage().getBody(ScenarioResponse.class).getId())
        .isEqualTo("scenario-handled-response");
  }

  @Test
  void WHEN_callingAutoOrchestratedScenario_THEN_MessagesAreProperlyReceived() {

    // arrange
    String payload = "Hi Adapter-";
    mockedLoggerConnector1.expectedMessageCount(1);
    mockedLoggerConnector1.expectedBodiesReceived(
        payload + AutoOrchestratedOutboundConnectorOne.ID);
    mockedLoggerConnector2.expectedMessageCount(1);
    mockedLoggerConnector2.expectedBodiesReceived(
        payload + AutoOrchestratedOutboundConnectorTwo.ID);

    // act
    Exchange exchange = template.withBody(payload).to(direct("autoOrchestratedInput")).send();

    // assert
    // TODO : should this be left on the exchange?
    assertThat(exchange.getMessage().getBody()).isInstanceOf(String.class);
    assertThat(exchange.getMessage().getBody(String.class))
        .isIn(
            payload + AutoOrchestratedOutboundConnectorOne.ID,
            payload + AutoOrchestratedOutboundConnectorTwo.ID);
  }
}
