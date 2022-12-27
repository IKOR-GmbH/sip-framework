package de.ikor.sip.foundation.core.scope.exchange;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
@Scope(value = "exchange", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ExchangeScopeBean {
  private Object internal;
}
