package de.ikor.sip.foundation.core.openapi;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.OpenAPI;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
      "camel.servlet.mapping.context-path=/adapter/*",
      "springdoc.api-docs.path=/api-docs",
      "springdoc.swagger-ui.path=/swagger-ui.html",
      "springdoc.show-actuator=false"
    })
@AutoConfigureMockMvc
class OpenApiContextPathResolverTest {

  @Autowired OpenAPI camelRestDSLOpenApi;
  @Autowired CamelContext camelContext;
  @Autowired ProducerTemplate producerTemplate;

  @Test
  void When_resolveCamelContextPathInOpenApi_Expect_ContextPathAdded() throws Exception {
    // arrange
    String endpointPath = "/getter";
    String contextPath = camelContext.getRestConfiguration().getContextPath();

    // act
    Exchange target =
        producerTemplate.send("rest-api://api-doc", exchange -> exchange.getMessage());
    String body = target.getMessage().getBody(String.class);

    // assert
    assertThat(body).contains("/getter").doesNotContain("/adapter/getter");
    assertThat(camelRestDSLOpenApi.getPaths()).containsKey(contextPath + endpointPath);
  }
}
