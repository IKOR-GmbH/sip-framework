package de.ikor.sip.foundation.core.openapi;

import static de.ikor.sip.foundation.core.CoreTestApplication.REST_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;

import de.ikor.sip.foundation.core.CoreTestApplication;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(
    classes = CoreTestApplication.class,
    properties = {"camel.servlet.mapping.context-path=/adapter/*"})
@DirtiesContext
class OpenApiContextPathResolverTest {

  @Autowired OpenAPI camelRestDSLOpenApi;
  @Autowired CamelContext camelContext;
  @Autowired ProducerTemplate producerTemplate;

  @Test
  void When_resolveCamelContextPathInOpenApi_Expect_ContextPathAdded() {
    // arrange
    String contextPath = camelContext.getRestConfiguration().getContextPath();

    // act
    Exchange target = producerTemplate.send("rest-api://api-doc", Exchange::getMessage);
    String body = target.getMessage().getBody(String.class);

    // assert
    assertThat(body).contains(REST_ENDPOINT).doesNotContain(contextPath + REST_ENDPOINT);
    assertThat(camelRestDSLOpenApi.getPaths()).containsKey(contextPath + REST_ENDPOINT);
  }
}
