package de.ikor.sip.foundation.core.apps.framework;

import de.ikor.sip.foundation.core.annotation.SIPIntegrationAdapter;
import de.ikor.sip.foundation.core.framework.CentralRouter;
import de.ikor.sip.foundation.core.framework.RouteStarter;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouter;
import org.springframework.context.annotation.Bean;

@SIPIntegrationAdapter
public class CentralRouterTestingApplication {
    @Bean
    CentralRouter testingCentralRouter() {
        return new TestingCentralRouter();
    }

    @Bean
    RouteStarter routeStarter() {return new RouteStarter();}
}
