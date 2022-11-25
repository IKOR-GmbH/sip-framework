package de.ikor.sip.foundation.core.apps.framework.centralrouter;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.framework.routers.CentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouterDefinition;
import de.ikor.sip.foundation.core.framework.AdapterRouteConfiguration;
import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouter;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;

@SIPIntegrationAdapter
public class CentralRouterTestingApplication {
  @Bean
  CentralRouterDefinition testingCentralRouter() {
    return new TestingCentralRouterDefinition();
  }

  @Bean
  CentralRouter configCentralRouter() {
    return new ConfigurationCentralRouter();
  }

  @Bean
  AdapterRouteConfiguration adapterRouteConfiguration() {
    return new TestAdapterConfiguration();
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
