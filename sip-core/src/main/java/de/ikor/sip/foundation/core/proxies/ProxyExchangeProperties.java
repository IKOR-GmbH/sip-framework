package de.ikor.sip.foundation.core.proxies;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/** Properties for defining behavior of an Exchange in {@link ProcessorProxy} */
@Data
public class ProxyExchangeProperties {
  private String proxyId;
  private Object body;
  private Map<String, Object> headers = new HashMap<>();
}
