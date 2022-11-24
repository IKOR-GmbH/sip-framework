package de.ikor.sip.foundation.core.framework.templates;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Component;

public class FromCentralRouterTemplate {
  private static final String USE_CASE_PARAM_KEY = "use-case";
  private FromCentralRouterTemplate() {}

  @Component
  public static class Template extends RouteBuilder {
    private static RouteDefinition definition;

    @Override
    public void configure() throws Exception {
      synchronized (this) {
        definition =
            routeTemplate("central-routing")
                .templateParameter(USE_CASE_PARAM_KEY)
                .from("sipmc:{{use-case}}")
                .routeId("sipmc-bridge-{{use-case}}");
      }
    }

    public static RouteDefinition getDefinition() {
      return definition;
    }
  }
}
