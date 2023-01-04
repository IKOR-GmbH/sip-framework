package de.ikor.sip.foundation.core.apps.declarative;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.direct;

import de.ikor.sip.foundation.core.apps.declarative.adapters.SimpleAdapter;
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

@CamelSpringBootTest
@SpringBootTest(classes = {SimpleAdapter.class})
@DisableJmx(false)
@MockEndpoints("log:message*")
public class BasicStructureTest {

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
  void when_PassthroughScenarioSendMessage_then_AdapterOutputsIt() {
    mockedLogger.expectedMessageCount(1);
    mockedLogger.expectedBodiesReceived("Hi Adapter");

    template.withBody("Hi Adapter").to(direct("trigger-passthrough")).send();
  }
}
