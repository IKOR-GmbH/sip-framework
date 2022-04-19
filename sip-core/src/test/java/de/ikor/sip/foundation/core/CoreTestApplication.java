package de.ikor.sip.foundation.core;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SIPIntegrationAdapter
public class CoreTestApplication {
  public static final String TEST_ROUTE_ID = "testRoute";
  public static final String TEST_ROUTE_ENDPOINT = "direct:test";

  @Configuration
  static class TestConfig {
    @Bean
    RoutesBuilder route() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          from(TEST_ROUTE_ENDPOINT).routeId(TEST_ROUTE_ID).to("mock:test");
        }
      };
    }
  }
}
