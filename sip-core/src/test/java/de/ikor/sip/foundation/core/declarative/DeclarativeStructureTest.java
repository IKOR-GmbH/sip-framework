package de.ikor.sip.foundation.core.declarative;

import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.declarative.DeclarativeStructureAdapter;
import org.apache.camel.EndpointInject;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.DisableJmx;
import org.apache.camel.test.spring.junit5.MockEndpoints;
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

  @EndpointInject("mock:log:message")
  private MockEndpoint mockedLogger;

  @BeforeEach
  void setup() {
    mockedLogger.reset();
  }

  @Test
  void when_integrationScenarioWithTwoOutboundConnectors_then_checkIfTheyShareSameSipmcChannel() {
    // arrange & act
    String sipmcUri1 =
        camelContext
            .getRoute("sip-connector_outboundConnectorTwo_scenarioTakeover")
            .getConsumer()
            .getEndpoint()
            .getEndpointUri();
    String sipmcUri2 =
        camelContext
            .getRoute("sip-connector_outboundConnectorOne_scenarioTakeover")
            .getConsumer()
            .getEndpoint()
            .getEndpointUri();

    // assert
    assertThat(sipmcUri1).isEqualTo(sipmcUri2);
  }
}
