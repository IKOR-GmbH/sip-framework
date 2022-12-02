package de.ikor.sip.foundation.core.framework.beans;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
@Scope(value = "conversation", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CDMHolder {
  private Object internal;
}
