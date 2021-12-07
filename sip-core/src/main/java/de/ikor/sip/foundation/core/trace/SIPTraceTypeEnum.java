package de.ikor.sip.foundation.core.trace;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SIPTraceTypeEnum {
    BOTH("0"),
    LOG("1"),
    MEMORY("2");

    public final String label;

}
