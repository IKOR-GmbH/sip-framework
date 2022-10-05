package de.ikor.sip.foundation.core.framework.stubs;

import de.ikor.sip.foundation.core.framework.InConnector;
import de.ikor.sip.foundation.core.framework.InEndpoint;

import static java.lang.String.format;

public class SimpleInConnector extends InConnector {
    private final InEndpoint ep;
    private final String name;
    public static SimpleInConnector withUri(String endpointUri) {
        return new SimpleInConnector(endpointUri);
    }

    @Override
    public void configure() {
        from(ep);
    }

    public String getName() {
        return name;
    }

    private SimpleInConnector (String endpointUri) {
        name = format("testing-connector-%s", System.nanoTime());
        ep = InEndpoint.instance(endpointUri);
    }
}
