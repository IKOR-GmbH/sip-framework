package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.InConnector;
import de.ikor.sip.foundation.core.framework.InEndpoint;
import de.ikor.sip.foundation.core.framework.OutEndpointBuilder;
import org.apache.camel.model.RouteDefinition;

import static de.ikor.sip.foundation.core.framework.OutEndpointBuilder.outEndpointBuilder;
import static org.apache.camel.builder.Builder.body;

public class ComplexInConnector extends InConnector {
  @Override
  public String getName() {
    return "complex-in-connector";
  }

  @Override
  public void configure() {
    from(InEndpoint.instance("direct:complex-connector", "complex-in-id"))
        .enrich(
            outEndpointBuilder("direct:test-enrich", "enrich-id"),
            (oldExchange, newExchange) ->
            {newExchange.getIn().setBody(oldExchange.getIn().getBody() + " enriched");
            return newExchange;});
  }

  @Override
  public void handleResponse(RouteDefinition route) {
    route.transform(body().append(" voila"));
  }
}
