package de.ikor.sip.foundation.core.proxies;

import lombok.Data;

/** Properties for pausing and replacing a route */
@Data
public class ProxyRouteProperties {
  private String routeId;
  private String from;
  private String to;
}
