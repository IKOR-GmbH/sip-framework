package de.ikor.sip.foundation.core.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import lombok.AllArgsConstructor;
import org.apache.camel.CamelContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@AllArgsConstructor
public class OpenApiContextPathResolver {

  private final OpenAPI camelRestDSLOpenApi;
  private final CamelContext camelContext;

  @EventListener(ApplicationReadyEvent.class)
  void resolveCamelContextPathInOpenApi() {
    String contextPath = camelContext.getRestConfiguration().getContextPath();
    Paths paths = new Paths();
    if (camelRestDSLOpenApi.getPaths() != null) {
      camelRestDSLOpenApi
          .getPaths()
          .forEach((path, pathItem) -> paths.put(contextPath.concat(path), pathItem));
      camelRestDSLOpenApi.setPaths(paths);
    }
  }
}
