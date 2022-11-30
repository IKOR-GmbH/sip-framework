package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.GlobalRoutesConfiguration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.apache.camel.model.RouteConfigurationDefinition;

import java.util.List;

public class Scenario {
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private String name;

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private Class<?> cdmRequestType;

    @Getter()
    @Setter(AccessLevel.PACKAGE)
    private Class<?> cdmResponseType;

    @Getter()
    @Setter(AccessLevel.PACKAGE)
    private RouteConfigurationBuilder scenarioRoutesConfiguration;

    Scenario(CentralRouterDefinition routerDefinition) {
        this.name = routerDefinition.getScenario();
        this.cdmRequestType = routerDefinition.getCentralModelRequestClass();
        this.cdmResponseType = routerDefinition.getCentralModelResponseClass();
    }

    void copyGlobalToScenarioConfiguration(
            GlobalRoutesConfiguration routesConfiguration) {
        List<RouteConfigurationDefinition> routeConfigurations =
                routesConfiguration
                        .getConfigurationBuilder()
                        .getRouteConfigurationCollection()
                        .getRouteConfigurations();

        scenarioRoutesConfiguration
                .getRouteConfigurationCollection()
                .setRouteConfigurations(routeConfigurations);
    }
}
