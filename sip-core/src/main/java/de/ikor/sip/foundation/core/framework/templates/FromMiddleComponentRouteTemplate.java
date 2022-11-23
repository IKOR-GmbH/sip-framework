package de.ikor.sip.foundation.core.framework.templates;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;

import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import de.ikor.sip.foundation.core.framework.routers.CDMValidator;
import java.util.stream.Stream;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.TemplatedRouteBuilder;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Component;

public class FromMiddleComponentRouteTemplate {

  public static final String USE_CASE_PARAM_KEY = "use-case";
  private OutConnectorDefinition[] outConnectors;
  private final String useCase;
  private String suffix;
  public Class<?> centralDomainRequest;
  private boolean isParallel;
  protected static final String URI_PREFIX = "direct:";

  private FromMiddleComponentRouteTemplate(String useCase) {
    this.useCase = useCase;
  }

  public static FromMiddleComponentRouteTemplateBuilder withUseCase(String useCase) {
    return new FromMiddleComponentRouteTemplateBuilder(useCase);
  }

  public FromMiddleComponentRouteTemplate withCentralDomainRequest(Class<?> requestType) {
    this.centralDomainRequest = requestType;
    return this;
  }

  public TemplatedRouteBuilder fromMCMulticastRoute(
      RouteDefinition routeDefinition) { // TODO remove after analysis
    return TemplatedRouteBuilder.builder(camelContext(), "sip-mc")
        .parameter(USE_CASE_PARAM_KEY, useCase)
        .handler(templateDef -> templateDef.setRoute(routeDefinition));
  }

  public TemplatedRouteBuilder fromMCMulticastRoute() {
    MulticastDefinition multicastDefinition = Template.getMulticastDefinition();
    Stream.of(outConnectors)
        .forEach(
            outConnector -> {
              multicastDefinition.to(URI_PREFIX + outConnector.getName() + suffix);
            });
    multicastDefinition.parallelProcessing(isParallel);
    multicastDefinition.end();

    return TemplatedRouteBuilder.builder(camelContext(), "sip-mc-multicast")
        .parameter(USE_CASE_PARAM_KEY, useCase)
        .parameter("central-domain-model", centralDomainRequest);
  }

  public FromMiddleComponentRouteTemplate outConnectors(OutConnectorDefinition[] outConnectors) {
    this.outConnectors = outConnectors;
    return this;
  }

  public FromMiddleComponentRouteTemplate inParallel(boolean b) {
    this.isParallel = b;
    return this;
  }

  @Component
  public static class Template extends RouteBuilder {
    private static MulticastDefinition multicastDefinition;

    @Override
    public void configure() throws Exception {
      synchronized (this) {
        multicastDefinition =
            routeTemplate("sip-mc-multicast")
                .templateParameter(USE_CASE_PARAM_KEY)
                .templateParameter("central-domain-model")
                .templateBean(
                    "CDMValidator",
                    CDMValidator.class,
                    rtc -> new CDMValidator((Class<?>) rtc.getProperty("central-domain-model")))
                .from("sipmc:{{use-case}}")
                .bean("{{CDMValidator}}")
                .routeId("sipmc-bridge-{{use-case}}")
                .multicast();
      }
    }

    public static MulticastDefinition getMulticastDefinition() {
      return multicastDefinition;
    }
  }

  public static class FromMiddleComponentRouteTemplateBuilder {
    private String useCase;

    private FromMiddleComponentRouteTemplateBuilder(String useCase) {
      this.useCase = useCase;
    }

    public FromMiddleComponentRouteTemplate withSuffix(String suffix) {
      FromMiddleComponentRouteTemplate template = new FromMiddleComponentRouteTemplate(useCase);
      template.suffix = suffix;
      return template;
    }

    public FromMiddleComponentRouteTemplate build() {
      return new FromMiddleComponentRouteTemplate(useCase);
    }
  }
}
