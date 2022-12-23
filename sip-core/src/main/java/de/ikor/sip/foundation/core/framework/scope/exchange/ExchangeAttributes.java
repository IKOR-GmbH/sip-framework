package de.ikor.sip.foundation.core.framework.scope.exchange;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;

@RequiredArgsConstructor
public class ExchangeAttributes {
  private final Exchange exchange;
  @Getter private final Map<String, Object> scopeBeans = new HashMap<>();

  public void putBeanInScope(String key, Object obj) {
    if (obj instanceof ExchangeScopeBean) {
      ((ExchangeScopeBean) obj).setInternal(exchange.getMessage().getBody());
    }
    this.scopeBeans.put(key, obj);
  }
}
