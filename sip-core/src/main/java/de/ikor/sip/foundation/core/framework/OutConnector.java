package de.ikor.sip.foundation.core.framework;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.RouteDefinition;

public abstract class OutConnector {

  public String getName() {
    return this.getClass().getSimpleName();
  }

  public abstract void configure(RouteDefinition route);

  public abstract void configureOnConnectorLevel();

//  protected OnExceptionDefinition onException(Class<? extends Throwable>... exceptions) {
////    routeBuilder = getRouteBuilderInstance();
//    OnExceptionDefinition last = null;
//
//    for (Class<? extends Throwable> ex : exceptions) {
//      last = (last == null ? this.routeBuilder.onException(ex) : last.onException(ex));
//    }
//    return last;
//  }
}
