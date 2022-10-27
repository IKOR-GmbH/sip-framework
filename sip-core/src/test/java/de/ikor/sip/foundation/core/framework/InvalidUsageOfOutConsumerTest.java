package de.ikor.sip.foundation.core.framework;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

@SpringBootTest(classes = InvalidUsageOfOutConsumerTest.CoreTestApplication.class)
class InvalidUsageOfOutConsumerTest {
  @Autowired CamelContext camelContext;
  @Autowired List<CentralRouter> centralRouters;

  @Test
  void when_OutEndpointIsUsedAsConsumer_then_RuntimeExceptionIsThrown() {
    // arrange
    RouteBuilder rb =
        new RouteBuilder() {
          @Override
          public void configure() {
            from(OutEndpoint.instance("direct:outEndpointAsConsumer", "ep-id")).to("mock:test");
          }
        };
    // act & assert
    assertThatThrownBy(() -> camelContext.addRoutes(rb))
        .hasCauseExactlyInstanceOf(IllegalAccessException.class);
  }

  @Test
  void when_MultipleCentralRoutersAreRegistered_then_ApplicationStartsSuccessfully() {
    assertThat(centralRouters).asList().hasSize(2);
  }

  @SIPIntegrationAdapter
  public static class CoreTestApplication {
    @Bean
    CentralRouter firstSpyCentralRouter() {
      return new SpyCentralRouter();
    }

    @Bean
    CentralRouter secondSpyCentralRouter() {
      return new SpyCentralRouter();
    }
  }

  private static class SpyCentralRouter extends CentralRouter {
    public static boolean isConfigured = false;

    @Override
    public String getScenario() {
      return "null";
    }

    @Override
    public void configure() throws Exception {
      isConfigured = true;
    }

    @Override
    public void configureOnCentralRouterLevel() {}
  }
}