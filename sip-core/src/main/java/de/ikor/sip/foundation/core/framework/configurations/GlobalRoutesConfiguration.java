package de.ikor.sip.foundation.core.framework.configurations;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousDummyRouteConfigurationBuilder;

import lombok.Getter;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.RouteConfigurationDefinition;

public abstract class GlobalRoutesConfiguration {
  @Getter
  RouteConfigurationBuilder configurationBuilder = anonymousDummyRouteConfigurationBuilder();

  public abstract void defineGlobalConfiguration();

  protected RouteConfigurationDefinition configuration() {
    return configurationBuilder.routeConfiguration();
  }
}
