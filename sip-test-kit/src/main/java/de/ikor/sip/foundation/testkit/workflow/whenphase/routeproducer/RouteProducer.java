package de.ikor.sip.foundation.testkit.workflow.whenphase.routeproducer;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;

/** Route producer interface */
public interface RouteProducer {

    /**
     * Sends request to route
     *
     * @param exchange {@link Exchange}
     * @param endpoint {@link Endpoint}
     * @return {@link Exchange} result of route execution
     */
    Exchange executeTask(Exchange exchange, Endpoint endpoint);
}
