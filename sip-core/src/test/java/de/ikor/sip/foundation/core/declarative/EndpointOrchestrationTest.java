package de.ikor.sip.foundation.core.declarative;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;
import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.declarative.SimpleAdapter;
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

@CamelSpringBootTest
@SpringBootTest(classes = {SimpleAdapter.class})
@DisableJmx(false)
@MockEndpoints("log:message*")
class EndpointOrchestrationTest {

  @EndpointInject("mock:log:message")
  private MockEndpoint mockedLogger;

  @Autowired private FluentProducerTemplate template;

  @BeforeEach
  void setup() {
    mockedLogger.reset();
  }

  @AfterEach
  void assertTest() throws InterruptedException {
    mockedLogger.assertIsSatisfied();
  }

  @Test
  void when_AppendingScenarioSendMessage_then_AdapterOutputsIt() {
    mockedLogger.expectedMessageCount(1);
    mockedLogger.expectedBodiesReceived("PRODUCED-Hi Adapter-CONSUMED");
    Exchange exchange = template.withBody("Hi Adapter").to(direct("triggerAdapter-append")).send();
    assertThat(exchange.getMessage().getBody(String.class))
        .contains("PRODUCED-Hi Adapter-CONSUMED-Handled");
  }
}
