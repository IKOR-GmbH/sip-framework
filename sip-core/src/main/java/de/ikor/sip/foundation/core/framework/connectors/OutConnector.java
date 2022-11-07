package de.ikor.sip.foundation.core.framework.connectors;

import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.RouteDefinition;

public abstract class OutConnector extends Connector {

  @Setter private RouteBuilder routeBuilder;

  public String getName() {
    return this.getClass().getSimpleName();
  }

  public abstract void configure(RouteDefinition route);

  public void configureOnException() {}

  protected OnExceptionDefinition onException(Class<? extends Throwable>... exceptions) {
    OnExceptionDefinition last = null;

    for (Class<? extends Throwable> ex : exceptions) {
      last = (last == null ? this.routeBuilder.onException(ex) : last.onException(ex));
    }
    return last;
  }
}
