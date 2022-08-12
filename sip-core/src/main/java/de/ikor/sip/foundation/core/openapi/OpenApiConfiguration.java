package de.ikor.sip.foundation.core.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import org.apache.camel.CamelContext;
import org.apache.camel.springboot.openapi.OpenApiAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@ConditionalOnClass(OpenApiAutoConfiguration.class)
public class OpenApiConfiguration {

    @Autowired OpenAPI camelRestDSLOpenApi;
    @Autowired CamelContext camelContext;

    @EventListener(ApplicationReadyEvent.class)
    void aCamelRestDSLOpenApi() {
        String contextPath = camelContext.getRestConfiguration().getContextPath();
        Paths paths = new Paths();
    if (camelRestDSLOpenApi != null &&  camelRestDSLOpenApi.getPaths() != null) {
      camelRestDSLOpenApi.getPaths().forEach((s, pathItem) -> paths.put(contextPath + s, pathItem));
      camelRestDSLOpenApi.setPaths(paths);
        }
    }


}
