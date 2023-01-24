package de.ikor.sip.foundation.core.declarative;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.http;

import de.ikor.sip.foundation.core.apps.declarative.SimpleAdapter;
import org.apache.camel.EndpointInject;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@CamelSpringBootTest
@SpringBootTest(
    classes = {SimpleAdapter.class},
    properties = {"camel.rest.binding-mode=auto"},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisableJmx(false)
@MockEndpoints("log:message*")
@DirtiesContext
public class EndpointOrchestrationTest {

  @EndpointInject("mock:log:message")
  private MockEndpoint mockedLogger;

  @Autowired private FluentProducerTemplate template;
  @LocalServerPort private int localServerPort;

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
    template.withBody("Hi Adapter").to(direct("triggerAdapter-append")).send();
  }

  @Test
  void When_UsingScenario_With_RestEndpoint_Then_RestRoutesAreCreatedAndConnectedToScenario() {
    mockedLogger.expectedBodiesReceivedInAnyOrder("Hi Adapter-CONSUMED");
    template
        .withBody("Hi Adapter")
        .to(http("localhost:" + localServerPort + "/adapter/path"))
        .send();
  }
}
