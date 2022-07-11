package de.ikor.sip.foundation.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FoundationFeature {
  TRACING("tracing"),
  INFO("info"),
  ADAPTER_ROUTES("adapter-routes"),
  HEALTH("health");

  @Getter private String value;
}
