package de.ikor.sip.foundation.core.framework.templates;

import de.ikor.sip.foundation.core.framework.routers.CDMValidator;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Component;

import static de.ikor.sip.foundation.core.framework.templates.FromMiddleComponentRouteTemplate.SUFFIX_PARAM_KEY;
import static de.ikor.sip.foundation.core.framework.templates.FromMiddleComponentRouteTemplate.USE_CASE_PARAM_KEY;

public class FromCentralRouterTemplate {

  @Component
  public static class Template extends RouteBuilder {
    private static RouteDefinition definition;

    @Override
    public void configure() throws Exception {
      synchronized (this) {
        definition =
            routeTemplate("central-routing")
                .templateParameter(USE_CASE_PARAM_KEY)
                .templateParameter(SUFFIX_PARAM_KEY, "")
                .from("sipmc:{{use-case}}{{suffix}}")
                .routeId("sipmc-bridge-{{use-case}}{{suffix}}");
      }
    }

    public static RouteDefinition getDefinition() {
      return definition;
    }
  }
}
