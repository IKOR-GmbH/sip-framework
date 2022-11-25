package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.AdapterRouteConfiguration;
import de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper;
import de.ikor.sip.foundation.core.framework.connectors.OutConnectorDefinition;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.anonymousDummyRouteConfigurationBuilder;

@Component
public class RouteStarter extends EventNotifierSupport {
  final List<CentralRouterDefinition> availableRouters;
  private Optional<AdapterRouteConfiguration> routeConfiguration;

  public RouteStarter(
          List<CentralRouterDefinition> centralRouters,
          Optional<AdapterRouteConfiguration> routeConfiguration) {
    this.availableRouters =
            centralRouters.stream()
                    .filter(router -> router.getClass().isAnnotationPresent(CentralRouterDomainModel.class))
                    .collect(Collectors.toList());
    this.routeConfiguration = routeConfiguration;
    this.routeConfiguration.ifPresent(AdapterRouteConfiguration::globalConfiguration);
  }

  @Override
  public void notify(CamelEvent event) {
    StaticRouteBuilderHelper.setCamelContext(
        ((CamelEvent.CamelContextInitializingEvent) event).getContext());
    availableRouters.forEach(this::configureDefinition);
    availableRouters.stream()
        .filter(rd -> Objects.nonNull(rd.getDefinition()))
        .map(CentralRouterDefinition::toCentralRouter)
        .forEach(this::buildRoutes);
  }

  void buildRoutes(CentralRouter router) {
    buildActiveRoutes(router);
  }

  public void configureDefinition(CentralRouterDefinition router) {
    try {
      router.defineTopology();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void buildActiveRoutes(CentralRouter router) {
    RouteConfigurationBuilder routeConfigurationBuilder =
            routeConfiguration
                    .map(AdapterRouteConfiguration::getConfigurationBuilder)
                    .orElse(anonymousDummyRouteConfigurationBuilder());
    RouteBinder actualRouteBinder =
        new RouteBinder(router.getScenario(), router.getCentralModelRequestClass(), routeConfigurationBuilder);
    routeConfiguration.ifPresent(router::addConfigToRouteBuilder);
    router.buildConfiguration();
    router.buildOnException();
    router.buildTopology();
    router
        .getOutTopologyDefinition()
        .forEach((outConnectors, s) -> bindOutConnectors(actualRouteBinder, outConnectors, s));
  }

  private void bindOutConnectors(
      RouteBinder routeBinder, OutConnectorDefinition[] outConnectors, String s) {
    if ("seq".equals(s)) {
      routeBinder.appendOutConnectorsSeq(outConnectors);
    } else if ("par".equals(s)) {
      routeBinder.appendOutConnectorsParallel(outConnectors);
    }
  }

  @Override
  public boolean isEnabled(CamelEvent event) {
    return event instanceof CamelEvent.CamelContextInitializingEvent;
  }
}
