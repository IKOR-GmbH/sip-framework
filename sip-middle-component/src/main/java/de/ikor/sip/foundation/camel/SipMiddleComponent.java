package de.ikor.sip.foundation.camel;

import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.boot.SpringBootCamelContext;
import org.apache.camel.support.DefaultComponent;

/** SIPMC component that provides SIP Platform specific internal routing to integration adapters. */
@org.apache.camel.spi.annotations.Component("sipmc")
public class SipMiddleComponent extends DefaultComponent {
  private static final String MIDDLE_COMPONENT_PREFIX = "seda:";

  /**
   * Creates the component
   *
   * @param context the camel context this component "belongs to"
   */
  public SipMiddleComponent(CamelContext context) {
    super(context);
  }

  @Override
  protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters)
      throws Exception {

    String targetEndpointUri =
        new StringBuilder()
            .append(MIDDLE_COMPONENT_PREFIX)
            .append(remaining)
            .append("?multipleConsumers=")
            .append(hasMultipleSipmcConsumers(uri))
            .append("&waitForTaskToComplete=always")
            .toString();

    return new SipMiddleEndpoint(uri, this, targetEndpointUri);
  }

  private boolean hasMultipleSipmcConsumers(String uri) {
    String strippedUri = uri.replace("//", "");
    return ((SpringBootCamelContext) getCamelContext())
            .getRouteDefinitions().stream()
                .map(RouteDefinition::getInput)
                .map(FromDefinition::getUri)
                .filter(strippedUri::equals)
                .count()
        > 1;
  }
}
