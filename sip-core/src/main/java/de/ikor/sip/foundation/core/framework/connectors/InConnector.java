package de.ikor.sip.foundation.core.framework.connectors;

import de.ikor.sip.foundation.core.framework.beans.CDMValueSetter;
import de.ikor.sip.foundation.core.framework.endpoints.EndpointDomainTransformation;
import de.ikor.sip.foundation.core.framework.endpoints.EndpointDomainValidation;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import de.ikor.sip.foundation.core.framework.endpoints.RestInEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;

import static de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister.getInEndpointUri;

public abstract class InConnector extends Connector {
  private InEndpoint inEndpoint;

  public abstract void configure();

  @Override
  public void configureOnException() {}

  public void handleResponse(RouteDefinition route) {}

  protected RouteDefinition from(InEndpoint inEndpoint) {
    this.inEndpoint = inEndpoint;
    RouteDefinition routeDefinition = routeBuilder.from(getInEndpointUri(inEndpoint.getId()));
    inEndpoint.getTransformFunction().ifPresent(function -> routeDefinition.process(new EndpointDomainTransformation<>(function)));
    inEndpoint.getDomainClassType().ifPresent(domainClassType -> routeDefinition.process(new EndpointDomainValidation(domainClassType, inEndpoint.getId())));
    return routeDefinition.bean(CDMValueSetter.class, "process");
  }

  protected RouteDefinition from(RestDefinition restDefinition) {
    restDefinition.to("direct:rest-" + inEndpoint.getUri());
    routeBuilder.getRestCollection().getRests().add(restDefinition);
    return routeBuilder.from("direct:rest-" + inEndpoint.getUri()).bean(CDMValueSetter.class, "process");
  }

  protected RestDefinition rest(String uri, String id) {
    RestInEndpoint restInEndpoint = RestInEndpoint.instance(uri, id);
    inEndpoint = restInEndpoint;
    return restInEndpoint.definition();
  }

  public String getEndpointUri() {
    return inEndpoint.getUri();
  }
}
