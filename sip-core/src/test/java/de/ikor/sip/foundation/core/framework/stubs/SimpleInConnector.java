package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.InConnector;
import de.ikor.sip.foundation.core.framework.InEndpoint;

import static java.lang.String.format;

public class SimpleInConnector extends InConnector {
    InEndpoint ep;
    private String name = format("testing-connector-%s", System.nanoTime());
    public SimpleInConnector (String endpointUri) {
        ep = InEndpoint.instance(endpointUri);
    }
    @Override
    public void configure() {
        from(ep);
    }

    public String getName() {
        return name;
    }
}
