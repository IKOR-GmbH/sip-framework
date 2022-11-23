package de.ikor.sip.foundation.core.framework.routers;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.apps.framework.centralrouter.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.templates.FromCentralRouterTemplate;
import de.ikor.sip.foundation.core.framework.templates.FromDirectOutConnectorRouteTemplate;
import de.ikor.sip.foundation.core.framework.templates.FromMiddleComponentRouteTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.TemplatedRouteBuilder;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.RouteDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = {CentralRouterTestingApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class RouteTemplatesTest {
  @Test
  void when_TryToCreateRouteFromSIPMCTemplate_then_TemplateBuilderExists() throws Exception {
    MulticastDefinition multicastDefinition =
        FromMiddleComponentRouteTemplate.Template.getMulticastDefinition();
    RouteBuilder testingRouteBuilder =
        new RouteBuilder() {
          @Override
          public void configure() throws Exception {
            multicastDefinition.to("direct:hi");
            TemplatedRouteBuilder someUseCase =
                TemplatedRouteBuilder.builder(camelContext(), "sip-mc-multicast")
                    .parameter("use-case", "someUseCase")
                    .parameter("central-domain-model", String.class.getCanonicalName());
            someUseCase.add();
          }
        };
    testingRouteBuilder.configure();
    assertThat(FromMiddleComponentRouteTemplate.Template.getMulticastDefinition()).isNotNull();
    assertThat(multicastDefinition).isNotNull();
  }

  @Test
  void when_TryToCreateRouteFromOutConnectorRouteTemplate_then_TemplateBuilderExists()
      throws Exception {
    RouteDefinition definition = FromDirectOutConnectorRouteTemplate.Template.getDefinition();
    RouteBuilder testingRouteBuilder =
        new RouteBuilder() {
          @Override
          public void configure() throws Exception {
            definition.to("direct:hi");
            TemplatedRouteBuilder someUseCase =
                TemplatedRouteBuilder.builder(camelContext(), "direct-out-connector")
                    .parameter("use-case", "someUseCase")
                    .parameter("out-connector-name", "someConnectorName");
            someUseCase.add();
          }
        };
    testingRouteBuilder.configure();
    assertThat(FromDirectOutConnectorRouteTemplate.Template.getDefinition()).isNotNull();
    assertThat(definition).isNotNull();
  }

  @Test
  void when_TryToCreateRouteFromCentralRouterRouteTemplate_then_TemplateBuilderExists()
      throws Exception {
    RouteDefinition definition = FromCentralRouterTemplate.Template.getDefinition();
    RouteBuilder testingRouteBuilder =
        new RouteBuilder() {
          @Override
          public void configure() throws Exception {
            definition.to("direct:hi");
            TemplatedRouteBuilder someUseCase =
                TemplatedRouteBuilder.builder(camelContext(), "central-routing")
                    .parameter("use-case", "someUseCase")
                    .parameter("out-connector-name", "someConnectorName");
            someUseCase.add();
          }
        };
    testingRouteBuilder.configure();
    assertThat(FromDirectOutConnectorRouteTemplate.Template.getDefinition()).isNotNull();
    assertThat(definition).isNotNull();
  }
}
