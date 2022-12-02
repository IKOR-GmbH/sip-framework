package de.ikor.sip.foundation.core.framework;

import static de.ikor.sip.foundation.core.apps.framework.ConfigurationTestingCentralRouter.SCENARIO_HEADER_KEY;
import static de.ikor.sip.foundation.core.apps.framework.ConfigurationTestingCentralRouter.SCENARIO_HEADER_VALUE;
import static de.ikor.sip.foundation.core.apps.framework.TestGlobalRoutesConfiguration.GLOBAL_HEADER_KEY;
import static de.ikor.sip.foundation.core.apps.framework.TestGlobalRoutesConfiguration.GLOBAL_HEADER_VALUE;

import de.ikor.sip.foundation.core.apps.core.CoreTestApplication;
import de.ikor.sip.foundation.core.apps.framework.ConfigurationTestingCentralRouter;
import de.ikor.sip.foundation.core.apps.framework.NoConfigurationTestingCentralRouter;
import de.ikor.sip.foundation.core.apps.framework.TestGlobalRoutesConfiguration;
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
@SpringBootTest(classes = {CoreTestApplication.class, NoConfigurationTestingCentralRouter.class, ConfigurationTestingCentralRouter.class, TestGlobalRoutesConfiguration.class})
@DisableJmx(false)
@MockEndpoints("seda:out.*config")
@DirtiesContext
class GlobalConfigTest {

  @Autowired ProducerTemplate producerTemplate;

  @EndpointInject("mock:seda:out-config")
  private MockEndpoint mock;

  @EndpointInject("mock:seda:out-no-config")
  private MockEndpoint noConfigMock;

  @Test
  void When_GlobalAndScenarioInterceptConfigured_Expect_InterceptTriggeredOnRequest()
      throws InterruptedException {
    mock.expectedHeaderReceived(GLOBAL_HEADER_KEY, GLOBAL_HEADER_VALUE);
    mock.expectedHeaderReceived(SCENARIO_HEADER_KEY, SCENARIO_HEADER_VALUE);

    producerTemplate.sendBody("seda:config", "");

    mock.assertIsSatisfied();
  }

  @Test
  void given_TwoScenariosAndOneScenarioLevelConfiguration_when_ScenarioWithoutConfigurationIsTriggered_expect_HeaderIsNotSet()
          throws InterruptedException {
    noConfigMock.expectedBodiesReceived("no-config ");

    producerTemplate.sendBody("seda:no-config", "");

    noConfigMock.assertIsSatisfied();
  }
}
