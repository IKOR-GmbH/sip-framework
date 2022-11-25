package de.ikor.sip.foundation.core.framework;

import static de.ikor.sip.foundation.core.apps.framework.ConfigurationTestingCentralRouter.SCENARIO_HEADER_KEY;
import static de.ikor.sip.foundation.core.apps.framework.ConfigurationTestingCentralRouter.SCENARIO_HEADER_VALUE;
import static de.ikor.sip.foundation.core.apps.framework.TestAdapterRouteConfiguration.GLOBAL_HEADER_KEY;
import static de.ikor.sip.foundation.core.apps.framework.TestAdapterRouteConfiguration.GLOBAL_HEADER_VALUE;

import de.ikor.sip.foundation.core.apps.framework.ConfigurationTestingCentralRouter;
import de.ikor.sip.foundation.core.apps.framework.TestAdapterRouteConfiguration;
import de.ikor.sip.foundation.core.apps.framework.centralrouter.CentralRouterTestingApplication;
import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.DisableJmx;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@CamelSpringBootTest
@SpringBootTest(classes = {CentralRouterTestingApplication.class, ConfigurationTestingCentralRouter.class, TestAdapterRouteConfiguration.class})
@DisableJmx(false)
@MockEndpoints("seda:out-config")
@DirtiesContext
public class GlobalConfigTest {

  @Autowired ProducerTemplate producerTemplate;

  @EndpointInject("mock:seda:out-config")
  private MockEndpoint mock;

  @Test
  void When_GlobalOrScenarioInterceptConfigured_Expect_InterceptTriggeredOnRequest()
      throws InterruptedException {
    mock.expectedHeaderReceived(GLOBAL_HEADER_KEY, GLOBAL_HEADER_VALUE);
    mock.expectedHeaderReceived(SCENARIO_HEADER_KEY, SCENARIO_HEADER_VALUE);

    producerTemplate.sendBody("seda:config", "");

    mock.assertIsSatisfied();
  }
}
