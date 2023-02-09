package de.ikor.sip.foundation.core.declarative.connectors;

import lombok.Getter;

/**
 * Enumeration for representing the type of endpoint.
 */
public enum ConnectorType {
    IN("in"),

    OUT("out");

    @Getter
    private final String value;

    ConnectorType(String value) {
        this.value = value;
    }
}
