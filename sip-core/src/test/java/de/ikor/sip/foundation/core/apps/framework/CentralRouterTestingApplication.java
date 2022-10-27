package de.ikor.sip.foundation.core.apps.framework;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouter;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;

@SIPIntegrationAdapter
public class CentralRouterTestingApplication {
  @Bean
  CentralRouter testingCentralRouter() {
    return new TestingCentralRouter();
  }

  @Bean
  CentralRouter restCentralRouter() {
    return new RestCentralRouter();
  }

  @Bean
  RouteBuilder setUpHelperRoutes() {
    return new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        from("direct:test-enrich").setBody(simple("yes, enrich works"));
      }
    };
  }
}
