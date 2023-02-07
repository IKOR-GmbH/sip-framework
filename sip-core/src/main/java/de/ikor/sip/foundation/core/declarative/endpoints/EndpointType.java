package de.ikor.sip.foundation.core.declarative.endpoints;

import lombok.Getter;

/** Enumeration for representing the type of endpoint. */
public enum EndpointType {
  IN("in"),

  OUT("out");

  @Getter private final String value;

  EndpointType(String value) {
    this.value = value;
  }
}
