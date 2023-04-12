package de.ikor.sip.foundation.core.declarative;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.ikor.sip.foundation.core.apps.declarative.DeclarativeStructureAdapter;
import de.ikor.sip.foundation.core.apps.declarative.DeclarativeStructureAdapter.ScenarioResponse;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
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
@SpringBootTest(classes = {DeclarativeStructureAdapter.class})
@DisableJmx(false)
@MockEndpoints("log:message*")
@DirtiesContext
class DeclarativeStructureTest {

  @Autowired private ExtendedCamelContext camelContext;

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
    // arrange & act
    mockedLoggerConnector1.expectedMessageCount(1);
    mockedLoggerConnector1.expectedBodiesReceived("Hi Adapter-scenarioprepared");
    mockedLoggerConnector2.expectedMessageCount(0);

    Exchange exchange = template.withBody("Hi Adapter").to(direct("dummyInputOne")).send();

    assertThat(exchange.getMessage().getBody()).isInstanceOf(ScenarioResponse.class);
    assertThat(exchange.getMessage().getBody(ScenarioResponse.class).getValue()).isEqualTo(1);
  }

  @Test
  void
      WHEN_callingSecondInboundConnector_THEN_OutboundConnector1And2ReceiveAMessageAndResponseIsAggregated() {
    // arrange & act
    mockedLoggerConnector1.expectedMessageCount(1);
    mockedLoggerConnector1.expectedBodiesReceived("Hi Adapter");
    mockedLoggerConnector2.expectedMessageCount(1);
    mockedLoggerConnector2.expectedBodiesReceived("Hi Adapter-scenarioprepared");

    Exchange exchange = template.withBody("Hi Adapter").to(direct("dummyInputTwo")).send();

    assertThat(exchange.getMessage().getBody()).isInstanceOf(ScenarioResponse.class);
    assertThat(exchange.getMessage().getBody(ScenarioResponse.class).getValue()).isEqualTo(120);
    assertThat(exchange.getMessage().getBody(ScenarioResponse.class).getId())
        .isEqualTo("scenario-handled-response");
  }
}
