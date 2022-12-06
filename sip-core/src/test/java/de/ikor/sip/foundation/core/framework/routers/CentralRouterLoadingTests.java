package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.apps.framework.centralrouter.CentralRouterTestingApplication;
import de.ikor.sip.foundation.core.framework.routers.CentralRouter;
import de.ikor.sip.foundation.core.framework.routers.RouteStarter;
import de.ikor.sip.foundation.core.framework.stubs.TestingCentralRouterAsBean;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static de.ikor.sip.foundation.core.framework.StaticRouteBuilderHelper.camelContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {CentralRouterTestingApplication.class, TestingCentralRouterAsBean.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CentralRouterLoadingTests {

    @Autowired
    private TestingCentralRouterAsBean routerSubject;

    @Autowired(required = false)
    private RouteStarter routeStarter;

    @Autowired
    private List<CentralRouter> centralRouters;

    @Test
    void when_ApplicationStarts_then_CentralRouterBeanIsLoaded() {
        // TODO this test should check on routerSubject bean availability. routerSubject should be bean
        assertThat(routerSubject).as("CentralRouter bean not initialized").isNotNull();
        Assertions.assertThat(camelContext()).as("Camel context not set on CentralRouter").isNotNull();
    }

    @Test
    void when_ApplicationStarts_then_CentralRouterBeanIsConfigured() throws Exception {
        assertThat(routerSubject.isConfigured).isTrue();
    }

    @Test
    void when_AppStarts_then_RouteStarterIsInitialized() {
        assertThat(routeStarter).as("RouteStarter bean is not initialized").isNotNull();
    }

    @Test
    void given_CentralRouterBeansArePresent_when_AppStarts_then_RoutStarterHasRouters() {
        assertThat(routeStarter.availableRouters).isNotEmpty();
    }
}
