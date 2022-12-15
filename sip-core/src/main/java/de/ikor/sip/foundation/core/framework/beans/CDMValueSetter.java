package de.ikor.sip.foundation.core.framework.beans;

import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CDMValueSetter {
  private final CDMHolder bean;

  public void process(Exchange exchange) {
    bean.setInternal(exchange.getMessage().getBody());
  }
}
