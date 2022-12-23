package de.ikor.sip.foundation.core.framework.stubs;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnrichRouteConfiguration {

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
