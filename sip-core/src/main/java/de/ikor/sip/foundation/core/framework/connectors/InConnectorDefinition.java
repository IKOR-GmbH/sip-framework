package de.ikor.sip.foundation.core.framework.connectors;

import static de.ikor.sip.foundation.core.framework.endpoints.CentralEndpointsRegister.getInEndpointUri;

import de.ikor.sip.foundation.core.framework.beans.CDMValueSetter;
import de.ikor.sip.foundation.core.framework.endpoints.EndpointDomainTransformation;
import de.ikor.sip.foundation.core.framework.endpoints.EndpointDomainValidation;
import de.ikor.sip.foundation.core.framework.endpoints.InEndpoint;
import de.ikor.sip.foundation.core.framework.endpoints.RestInEndpoint;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestDefinition;

public abstract class InConnectorDefinition extends Connector {
  @Getter @Setter private RouteBuilder routeBuilder;
  private InEndpoint inEndpoint;
  @Getter private RouteDefinition routeDefinition;

  public abstract void configure();

  public void configureOnException() {}

  public void handleResponse(RouteDefinition route) {}

  protected RouteDefinition from(InEndpoint inEndpoint) {
    this.inEndpoint = inEndpoint;
    routeDefinition = initDefinition();
    routeDefinition.from(getInEndpointUri(inEndpoint.getId()));
    inEndpoint.getTransformFunction().ifPresent(function -> routeDefinition.process(new EndpointDomainTransformation<>(function)));
    inEndpoint.getDomainClassType().ifPresent(domainClassType -> routeDefinition.process(new EndpointDomainValidation(domainClassType, inEndpoint.getId())));
    return routeDefinition.bean(CDMValueSetter.class, "process");
  }

  private RouteDefinition initDefinition() {
    return routeDefinition == null ? new RouteDefinition() : routeDefinition;
  }

  protected RouteDefinition from(RestDefinition restDefinition) {
    restDefinition.to("direct:rest-" + inEndpoint.getUri());
    routeDefinition = initDefinition();
    routeBuilder.getRestCollection().getRests().add(restDefinition);
    return routeDefinition.from("direct:rest-" + inEndpoint.getUri()).bean(CDMValueSetter.class, "process");
  }

  protected RestDefinition rest(String uri, String id) {
    RestInEndpoint restInEndpoint = RestInEndpoint.instance(uri, id);
    inEndpoint = restInEndpoint;
    return restInEndpoint.definition();
  }

  protected OnExceptionDefinition onException(Class<? extends Throwable>... exceptions) {
    routeDefinition = initDefinition();
    OnExceptionDefinition last = null;

    for (Class<? extends Throwable> ex : exceptions) {
      last = (last == null ? this.routeDefinition.onException(ex) : last.onException(ex));
    }
    return last;
  }

  public String getEndpointUri() {
    return inEndpoint.getUri();
  }

  public void setDefinition() {
    this.routeDefinition = new RouteDefinition();
  }
}
