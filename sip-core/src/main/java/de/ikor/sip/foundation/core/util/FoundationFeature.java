package de.ikor.sip.foundation.core.util;

public enum FoundationFeature {
    TRACING("tracing"),
    INFO("info"),
    ADAPTER_ROUTES("adapter-routes"),
    HEALTH("health");

    private String value;

    FoundationFeature(String value) {
        this.value = value;
    }
}
