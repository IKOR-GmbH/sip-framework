package de.ikor.sip.foundation.core.framework;

import lombok.Getter;

public class InEndpoint {
    @Getter
    private String uri;
    public static InEndpoint instance(String uri) {
        return new InEndpoint(uri);
    }

    private InEndpoint(String uri) {
        this.uri = uri;
    }
}
