package de.ikor.sip.foundation.core.framework;

import static de.ikor.sip.foundation.core.framework.routers.CentralRouter.anonymousDummyRouteConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.RouteConfigurationDefinition;

public abstract class AdapterRouteConfiguration {
  @Getter
  RouteConfigurationBuilder configurationBuilder = anonymousDummyRouteConfigurationBuilder();

  @Getter List<RouteConfigurationDefinition> routeConfigurationDefinitions = new ArrayList<>();

  public abstract void globalConfiguration();

  protected RouteConfigurationDefinition configuration() {
    RouteConfigurationDefinition routeConfigurationDefinition =
        configurationBuilder.routeConfiguration();
    routeConfigurationDefinitions.add(routeConfigurationDefinition);
    return routeConfigurationDefinition;
  }
}
