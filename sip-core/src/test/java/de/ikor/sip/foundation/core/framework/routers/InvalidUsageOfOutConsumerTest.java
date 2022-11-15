package de.ikor.sip.foundation.core.framework.routers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.framework.endpoints.OutEndpoint;

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
  @Autowired List<CentralRouterDefinition> centralRouters;

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
    assertThat(centralRouters).asList().size().isGreaterThan(1);
  }

  @SIPIntegrationAdapter
  public static class CoreTestApplication {
    @Bean
    CentralRouterDefinition firstSpyCentralRouter() {
      return new SpyCentralRouterDefinition();
    }

    @Bean
    CentralRouterDefinition secondSpyCentralRouter() {
      return new SpyCentralRouterDefinition();
    }
  }

  @CentralRouterDomainModel
  private static class SpyCentralRouterDefinition extends CentralRouterDefinition {
    public static boolean isConfigured = false;

    @Override
    public String getScenario() {
      return "null";
    }

    @Override
    public void defineTopology() throws Exception {
      isConfigured = true;
    }

    @Override
    public void configureOnException() {}
  }
}
