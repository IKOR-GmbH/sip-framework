package de.ikor.sip.foundation.core.declarative;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;

import de.ikor.sip.foundation.core.apps.declarative.DeclarativeStructureAdapter;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.FluentProducerTemplate;
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

  @Autowired private FluentProducerTemplate template;

  @EndpointInject("mock:log:messageGroup2")
  private MockEndpoint mockedLoggerGroup2;

  @EndpointInject("mock:log:messageGroup3")
  private MockEndpoint mockedLoggerGroup3;

  @BeforeEach
  void setup() {
    mockedLoggerGroup2.reset();
    mockedLoggerGroup3.reset();
  }

  @Test
  void when_integrationScenarioWithTwoOutboundConnectors_then_checkIfTheyShareSameSipmcChannel()
      throws InterruptedException {
    // arrange & act
    mockedLoggerGroup2.expectedMessageCount(1);
    mockedLoggerGroup2.expectedBodiesReceived("Hi Adapter");
    mockedLoggerGroup3.expectedMessageCount(1);
    mockedLoggerGroup3.expectedBodiesReceived("Hi Adapter");

    Exchange exchange = template.withBody("Hi Adapter").to(direct("dummyInput")).send();

    mockedLoggerGroup2.assertIsSatisfied();
    mockedLoggerGroup3.assertIsSatisfied();
  }
}
