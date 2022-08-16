package de.ikor.sip.foundation.core.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.apache.camel.CamelContext;
import org.junit.jupiter.api.Test;

class OpenApiContextPathResolverTest {

  @Test
  void When_resolveCamelContextPathInOpenApi_Expect_ContextPathAdded() {
    // arrange
    OpenAPI openAPI = new OpenAPI();
    CamelContext camelContext = mock(CamelContext.class, RETURNS_DEEP_STUBS);
    Paths paths = new Paths();
    String endpointPath = "path";
    String contextPath = "context";
    paths.addPathItem(endpointPath, new PathItem());
    openAPI.setPaths(paths);
    when(camelContext.getRestConfiguration().getContextPath()).thenReturn(contextPath);
    OpenApiContextPathResolver subject = new OpenApiContextPathResolver(openAPI, camelContext);

    // act
    subject.resolveCamelContextPathInOpenApi();

    // assert
    assertThat(openAPI.getPaths()).containsKey(contextPath + endpointPath);
  }
}
