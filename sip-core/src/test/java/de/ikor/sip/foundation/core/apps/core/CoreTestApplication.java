package de.ikor.sip.foundation.core.apps.core;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@SIPIntegrationAdapter
public class CoreTestApplication {
  public static final String TEST_ROUTE_ID = "testRoute";
  public static final String REST_ENDPOINT = "/getter";

  @Configuration
  static class TestConfig {

    @Autowired private Environment env;

    @Bean
    RoutesBuilder route() {
      return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
          restConfiguration()
              .component("servlet")
              .host("localhost")
              .port(env.getProperty("server.port", "8080"))
              .contextPath("/adapter")
              .apiContextPath("/api-doc")
              .apiProperty("api.title", "User API")
              .apiProperty("api.version", "1.0.0");
          rest().get(REST_ENDPOINT).id(TEST_ROUTE_ID).to("mock:rest");
          ;
        }
      };
    }
  }
}
