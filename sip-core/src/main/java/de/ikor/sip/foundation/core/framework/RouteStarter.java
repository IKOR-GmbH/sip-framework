package de.ikor.sip.foundation.core.framework;

import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteStarter extends EventNotifierSupport {
    @Autowired
    List<CentralRouter> availableRouters;

    @Override
    public void notify(CamelEvent event) throws Exception {
        CentralRouter.setCamelContext(((CamelEvent.CamelContextInitializingEvent) event).getContext());
        availableRouters.forEach(this::invokeConfigureMethod);
    }

    private void invokeConfigureMethod(CentralRouter router) {
        try {
            router.configure();
        } catch (Exception e) {
            throw new RuntimeException(e);//TODO implement or reuse existing exception
        }
    }

    private void callConfigureMethodOnEach(List<CentralRouter> availableRouters) throws Exception {
        for (CentralRouter router : this.availableRouters) {
            router.configure();
        }
    }

    @Override
    public boolean isEnabled(CamelEvent event) {
        return event instanceof CamelEvent.CamelContextInitializingEvent;
    }
}
