package de.ikor.sip.foundation.core.framework.connectors;

public interface Connector {
  String getName();

  default void configureOnException() {}
}
